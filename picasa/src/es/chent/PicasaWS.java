package es.chent;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import android.content.Context;
import android.util.Log;

public class PicasaWS extends DefaultHandler implements PicasaIfz {

	private static final String LOG_TAG = "[PICASAWS]";

	private final static String ENTRY_TAG = "entry";
	private final static String TITLE_TAG = "title";
	private final static String ID_TAG = "gphoto:id";
	private final static String CONTENT_TAG = "content";
	
	
	private Map<String, String> albumsIdsByAlbumTitles = new HashMap<String, String>();

	private Context appContext;

	private String authString;

	private String userID;
	
	private boolean attachedToGoogle;

	public Map<String, String> getAlbumsIdsByAlbumTitles() {
		String url_string = "http://picasaweb.google.com/data/feed/api/user/"
				+ userID;

		
		Log.d(LOG_TAG,"Requesting albums for: "+url_string);
		
		Map<String, String> headerMap = new HashMap<String, String>();
		headerMap.put("Authorization", "GoogleLogin auth=" + this.authString);
		headerMap.put("GData-Version", "2");
		GetClient picasaGETService = new GetClient(url_string, headerMap);
		InputStream inputStream = null;

		try {
			inputStream = picasaGETService.getInputStream();

			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser sp = spf.newSAXParser();
			XMLReader xr = sp.getXMLReader();
			xr.setContentHandler(new PicasaWSAlbumHandler());
			InputStream openStream = inputStream;
			xr.parse(new InputSource(openStream));
			Log.d(LOG_TAG, "Parsed!");
			openStream.close();
			return albumsIdsByAlbumTitles;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	public void connectGoogle(String user, String passwd) {

		attachedToGoogle = false;
		
		Log.d(LOG_TAG, "Connecting picasa for user: " + user+"  password: "+passwd);
		
		String url = "https://www.google.com/accounts/ClientLogin";
		DefaultHttpClient httpclient = new DefaultHttpClient();
		HttpResponse response;
		try {
			HttpPost httpost = new HttpPost(new URI(url));
			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			nvps.add(new BasicNameValuePair("accountType", "GOOGLE"));
			nvps.add(new BasicNameValuePair("Email", userID));
			nvps.add(new BasicNameValuePair("Passwd", passwd));
			nvps.add(new BasicNameValuePair("service", "lh2"));
			nvps.add(new BasicNameValuePair("source",
					"companyName-applicationName-1.0"));

			httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));

			// Post, check and show the result (not really spectacular, but
			// works):
			response = httpclient.execute(httpost);
			HttpEntity entity = response.getEntity();

			Log.d(LOG_TAG,
					"Google Login auth result = " + response.getStatusLine());

			if (entity != null) {
				InputStream toto = entity.getContent();
				long content_length = entity.getContentLength();
				StringBuffer content_buf = new StringBuffer();
				for (long i = 0; i < content_length; i++) {
					content_buf.append(((char) toto.read()));
				}

				int index = content_buf.toString().indexOf("Auth=");
				if (index != -1) {
					authString = content_buf.toString().substring(
							index + "Auth=".length(),
							content_buf.toString().length() - 1);
					
					attachedToGoogle = true;
				}
				entity.consumeContent();
			} else {
				Log.d(LOG_TAG, "Entity is null!");
			}

		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			httpclient.getConnectionManager().closeExpiredConnections();
		}

		Log.d(LOG_TAG, "Connected picasa for user " + user);

		
	}
	

	public PicasaWS(String user, String passwd, Context appContext) {
		
		

		this.appContext = appContext;

		userID = user;
			
	
		this.connectGoogle(user, passwd);
		
	}

	public void createAlbum(String album_name) {
		if (authString == null) {
			return;
		}
		String url = "http://picasaweb.google.com/data/feed/api/user/" + userID;
		// HttpParams params = new BasicHttpParams();
		// HttpProtocolParams.setVersion( params, HttpVersion.HTTP_1_1);
		// HttpProtocolParams.setContentCharset( params, "UTF-8");
		// HttpProtocolParams.setUseExpectContinue( params, false);
		//
		// HttpClient httpclient = new DefaultHttpClient( params);
		HttpClient httpclient = new DefaultHttpClient();
		HttpResponse response;
		HttpPost httpPost = new HttpPost(url);
		try {

			Header[] headers = new BasicHeader[2];
			headers[0] = new BasicHeader("Content-Type", "application/atom+xml");
			headers[1] = new BasicHeader("Authorization", "GoogleLogin auth="
					+ authString);
			httpPost.addHeader(headers[0]);
			httpPost.addHeader(headers[1]);

			String content_string = "<entry xmlns='http://www.w3.org/2005/Atom'"
					+ " xmlns:media='http://search.yahoo.com/mrss/'"
					+ " xmlns:gphoto='http://schemas.google.com/photos/2007'>"
					+ " <title type='text'>"
					+ album_name
					+ "</title>"
					+ " <gphoto:access>public</gphoto:access>"
					+ "<category scheme='http://schemas.google.com/g/2005#kind'"
					+ " term='http://schemas.google.com/photos/2007#album'>"
					+ "</category>" + "</entry>";

			httpPost.setEntity(new StringEntity(content_string));

			// Post, check and show the result (not really spectacular, but
			// works):
			response = httpclient.execute(httpPost);
			HttpEntity entity = response.getEntity();
			Log.d(LOG_TAG, "Create Album Result: " + response.getStatusLine());

			if (entity != null) {
				InputStream toto = entity.getContent();
				long content_length = entity.getContentLength();
				StringBuffer content_buf = new StringBuffer();
				for (long i = 0; i < content_length; i++) {
					content_buf.append(((char) toto.read()));
				}
				Log.d(LOG_TAG, "Content = [" + content_buf.toString() + "]");

				entity.consumeContent();
			} else {
				Log.d(LOG_TAG, "Entity is null!");
			}

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			httpclient.getConnectionManager().closeExpiredConnections();
		}
	}

	

	@Override
	public List<String> getAlbumPhotoUrls(String albumId) {
		
		
		
		String url_string = "http://picasaweb.google.com/data/feed/api/user/"
				+ userID+"/albumid/"+albumId;
		 
		
		Log.d(LOG_TAG,"Requesting photos for: "+url_string);
		
		Map<String, String> headerMap = new HashMap<String, String>();
		headerMap.put("Authorization", "GoogleLogin auth=" + this.authString);
		headerMap.put("GData-Version", "2");
		GetClient picasaGETService = new GetClient(url_string, headerMap);
		InputStream inputStream = null;

		try {
			inputStream = picasaGETService.getInputStream();

			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser sp = spf.newSAXParser();
			XMLReader xr = sp.getXMLReader();
			
			PicasaWSPhotoHandler picasaWSPhotoHandler = new PicasaWSPhotoHandler(); 
			
			xr.setContentHandler(picasaWSPhotoHandler);
			InputStream openStream = inputStream;
			xr.parse(new InputSource(openStream));
			Log.d(LOG_TAG, "Photos Parsed!");
			openStream.close();
			return picasaWSPhotoHandler.getPhotoUrls();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}	}

	
	public void addPicture(byte[] picture_data, String album_name) {

		if (picture_data == null || album_name == null) {
			return;
		}

		Log.d(LOG_TAG, "AddPicture, size: " + picture_data.length + ", album: "
				+ album_name);

		//NOT WORKING, hash CHANGED!!!!
		Map<String, String> lookup = getAlbumsIdsByAlbumTitles();
		String album_id = lookup.get(album_name);

		if (album_id == null) {
			Log.d(LOG_TAG, album_name + "album_id not found, create album!");
			createAlbum(album_name);
			lookup = getAlbumsIdsByAlbumTitles();
			try {
				// Wait 3s to make sure album is created
				Thread.sleep(3000l);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			album_id = lookup.get(album_name);
			if (album_id == null) {
				Log.d(LOG_TAG, "album id " + album_id + "  not found! ABORT!");
				return;
			}
		}

		String url = "http://picasaweb.google.com/data/feed/api/user/" + userID
				+ "/albumid/" + album_id;

		Log.d(LOG_TAG, "Try to upload picture to: " + url);

		HttpParams params = new BasicHttpParams();
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(params, "UTF-8");
		HttpProtocolParams.setUseExpectContinue(params, false);

		HttpClient httpclient = new DefaultHttpClient(params);
		HttpPost httppost = new HttpPost(url);
		try {
			Header[] headers = new BasicHeader[2];
			headers[0] = new BasicHeader("Content-Type", "image/jpeg");
			headers[1] = new BasicHeader("Authorization", "GoogleLogin auth="
					+ authString);
			httppost.addHeader(headers[0]);
			httppost.addHeader(headers[1]);

			ByteArrayEntity byteArrayEntity = new ByteArrayEntity(picture_data);
			byteArrayEntity.setContentType(headers[1]);
			httppost.setEntity(byteArrayEntity);

			HttpResponse response;
			response = httpclient.execute(httppost);

			HttpEntity entity = response.getEntity();
			if (entity != null) {
				Log.d(LOG_TAG,
						"Add Picture Result: " + response.getStatusLine());
				entity.consumeContent();
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			httpclient.getConnectionManager().closeExpiredConnections();
		}

	}

	private class PicasaWSPhotoHandler extends DefaultHandler {

		
		private String currentPhotoUrl;

		private boolean inEntryTag = false;
		
		private List<String> photoUrls = new ArrayList();

		public List<String> getPhotoUrls() {
			return photoUrls;
		}
		
		public void startElement(String uri, String name, String qName,
				Attributes atts) {
		
			
			if (ENTRY_TAG.equals(qName.trim())) {
			
				inEntryTag = true;
			} else if (inEntryTag && CONTENT_TAG.equals(qName.trim())) {
				
				String srcPhoto = atts.getValue("src");
				
				if (srcPhoto!=null) {
					currentPhotoUrl = srcPhoto;
				}
				
			}
		}

		public void endElement(String uri, String name, String qName)
				throws SAXException {

				
			if (ENTRY_TAG.equals(qName.trim())) {
				inEntryTag = false;
				if (currentPhotoUrl != null ) {
					Log.d(LOG_TAG,"Added photo: "+currentPhotoUrl);
					photoUrls.add(currentPhotoUrl);
					currentPhotoUrl  = null;
				}
			}
		}

		/*
		public void characters(char ch[], int start, int length) {

			String chars = (new String(ch).substring(start, start + length));
			Log.d(LOG_TAG, "Content: " + chars);
			if (currentElementName == null) {
				return;
			}

			if (inEntryTag && CONTENT_TAG.equals(currentElementName)) {
				currentPhotoUrl = chars;
			
		}
		 */
		
	}

	private class PicasaWSAlbumHandler extends DefaultHandler {

		private String currentElementName;
		private String currentEntryID;
		private String currentEntryTitle;

		private boolean inEntryTag = false;

		public void startElement(String uri, String name, String qName,
				Attributes atts) {
			currentElementName = qName.trim();
			
			if (ENTRY_TAG.equals(qName.trim())) {
				Log.d(LOG_TAG, "Entry!!!!!: " + currentElementName);
				inEntryTag = true;
			}
		}

		public void endElement(String uri, String name, String qName)
				throws SAXException {

			
			currentElementName = null;
			if (ENTRY_TAG.equals(qName.trim())) {
				inEntryTag = false;
				if (currentEntryID != null && currentEntryTitle != null) {
					albumsIdsByAlbumTitles.put(currentEntryID,
							currentEntryTitle);
				}
			}
		}

		public void characters(char ch[], int start, int length) {

			
			
			if (currentElementName == null) {
				return;
			}
			String chars = (new String(ch).substring(start, start + length));
			if (inEntryTag && ID_TAG.equals(currentElementName)) {
				currentEntryID = chars;
			} else if (inEntryTag && TITLE_TAG.equals(currentElementName)) {
				currentEntryTitle = chars;
			}
		}
	}

	@Override
	public boolean isAttached() {
		return attachedToGoogle;
	}
}	

