package es.chent;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

public class GetClient {
  private HttpClient httpClient = new DefaultHttpClient(); 
  private String url;
  private Map<String,String> headerMaps;
  private HttpResponse httpResponse;
  public GetClient() {
    super();
    
  }

  public GetClient(String url, Map<String, String> headerMaps) {
    super();
    this.url = url;
    this.headerMaps = headerMaps;
  }
  
  public InputStream getInputStream() throws Exception{
    HttpGet httpGet = new HttpGet(url);
    for (String headerName  : headerMaps.keySet()) {
      httpGet.setHeader(headerName,headerMaps.get(headerName));
    }
    
    
    httpResponse = null;
    try {
      httpResponse = httpClient.execute(httpGet);
    } catch (ClientProtocolException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      throw e;
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      throw e;
    }
    InputStream inputStream = null;
    //check the status code and get the strream
    if(httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
    {
      
      try {
        inputStream = httpResponse.getEntity().getContent();  
        
        } catch (IllegalStateException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
        int responseCode = httpResponse.getStatusLine().getStatusCode();
        String responseMsg = httpResponse.getStatusLine().getReasonPhrase();
        throw new Exception("Unable to get the connection from url ="+url+" Respons-Code="+responseCode+", Reason="+responseMsg);
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
        int responseCode = httpResponse.getStatusLine().getStatusCode();
        String responseMsg = httpResponse.getStatusLine().getReasonPhrase();
        throw new Exception("Unable to get the connection from url ="+url+" Respons-Code="+responseCode+", Reason="+responseMsg);
      }
      
    }
    else
    {
      int responseCode = httpResponse.getStatusLine().getStatusCode();
      String responseMsg = httpResponse.getStatusLine().getReasonPhrase();
      throw new Exception("Unable to get the connection from url ="+url+" Respons-Code="+responseCode+", Reason="+responseMsg);
    }
    
    return inputStream;
  }
  
  

}