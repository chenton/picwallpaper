package es.chent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
/*
import app.droid.picasa.albumservice.AlbumParser;
import app.droid.picasa.framework.httpservice.PicasaGETService;
import app.droid.picasa.framework.parser.PhotoParser;
import app.droid.picasa.model.Album;
import app.droid.picasa.model.Photo;
*/
public class PicasaServiceClient {
  
  public static String ENCODING = "application/xml";
  public static String URL_GET_ALBUM_LIST = "http://picasaweb.google.com/data/feed/api/user/default";
 /*
  private PhotoParser photoParser = null;
  private AlbumParser albumParser = null;
  */
  public static boolean addAlbum(String albumTitle,String albumDesc,String userName,String authToken){
    HttpClient httpClient = new DefaultHttpClient();
    HttpPost httpPost = new HttpPost("http://picasaweb.google.com/data/feed/api/user/"+userName);
    httpPost.setHeader("Authorization", "GoogleLogin auth="+authToken);
    httpPost.setHeader("GData-Version", "2");
    HttpResponse httpResponse = null;
    httpPost.setHeader("Content-type","application/atom+xml; charset=UTF-8");
    String xmlrequest = "<entry xmlns=\'http://www.w3.org/2005/Atom\' xmlns:media=\'http://search.yahoo.com/mrss/\' xmlns:gphoto=\'http://schemas.google.com/photos/2007\'>" +
        "<title type='text'>"+albumTitle+"</title>" +
        "<summary type='text'>"+albumDesc+"</summary>" +
//        "<gphoto:location>Italy</gphoto:location>" +
//        "<gphoto:access>public</gphoto:access>" +
        "<gphoto:timestamp>"+new Date().getTime()+"</gphoto:timestamp>" +
//        "<media:group>" +
//        "<media:keywords>italy, vacation</media:keywords>" +
//        "</media:group>" +
        "<category scheme=\'http://schemas.google.com/g/2005#kind\' term=\'http://schemas.google.com/photos/2007#album\'></category>" +
        "</entry>";
    
    StringEntity stringEntity = null;
    try {
      stringEntity = new StringEntity(xmlrequest,HTTP.UTF_8);
    } catch (UnsupportedEncodingException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
      return false;
    }
    
  
    
    
    httpPost.setEntity(stringEntity);
    try {
      
      httpResponse = httpClient.execute(httpPost);
    } catch (ClientProtocolException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
      System.out.println("Add album response: "+httpResponse.getStatusLine().getStatusCode()+" # "+httpResponse.getStatusLine().getReasonPhrase());
    return (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_CREATED);
    //check the status code and get the strream
    /**
    if(httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_CREATED)
    {
      InputStream inputStream = null;
      try {
        inputStream = httpResponse.getEntity().getContent();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String respoString  = "";
        System.out.println("Response Starts");
        
        AlbumParser parser = new AlbumParser(reader);
      Album albumList [] = parser.albumTitleList();
      //  PrintWriter pw  = new PrintWriter(new File("/home/Manish/picasa/album.xml"));
      while ((respoString = reader.readLine())!=null) {
        System.out.println(respoString);
        //pw.write(respoString);
        }
        //System.out.println("Album-List = "+albumList.toString());
      return albumList;
      } catch (IllegalStateException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      try {
        inputStream.close();
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      finally{
        inputStream  = null;
      }
      
      
      
    }
    else
    {
      
    }
    

    
    return false;**/
  }
  
  public static boolean deleteAlbum(String userName,String authToken){
    HttpClient httpClient = new DefaultHttpClient();
    HttpDelete httpDelete = new HttpDelete("http://picasaweb.google.com/data/feed/api/user/"+userName);
      return false;  
    }
  




/*
public Photo getNextPhotoForAlbum(String albumId) {
  // TODO Auto-generated method stub
  if(photoParser == null || photoParser.getReader() == null){
  Map<String,String> headerMap = new HashMap<String,String>();
  
  headerMap.put("Authorization", "GoogleLogin auth="+Picasa.authToken);
  headerMap.put("GData-Version", "2");
  PicasaGETService picasaGETService = new PicasaGETService("https://picasaweb.google.com/data/feed/api/user/default/albumid/"+albumId, headerMap);
  InputStream inputStream = null;
    
      try {
        inputStream = picasaGETService.getInputStream();
        
      } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      
      BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
      System.out.println("Response Starts");
      this.photoParser = new PhotoParser(reader);
      
  }
   return   photoParser.nextPhoto(true);
  
  
}

public void finishPhotoConnectionStream(){
  this.photoParser.finish();
}

public void finishAlbumConnectionStream(){
  this.albumParser.finish();
}

public Album getNextAlbum() {
  // TODO Auto-generated method stub
  if(albumParser == null){
  Map<String,String> headerMap = new HashMap<String,String>();
  
  headerMap.put("Authorization", "GoogleLogin auth="+Picasa.authToken);
  headerMap.put("GData-Version", "2");
  PicasaGETService picasaGETService = new PicasaGETService("https://picasaweb.google.com/data/feed/api/user/default", headerMap);
  InputStream inputStream = null;
    
      try {
        inputStream = picasaGETService.getInputStream();
        
      } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      
      BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
      System.out.println("Response Starts");
       albumParser = new AlbumParser(reader);
      
  }
   return   albumParser.nextAlbum();
  
  
}
*/

}