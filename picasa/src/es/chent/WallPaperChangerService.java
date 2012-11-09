package es.chent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

public class WallPaperChangerService extends Service {

	
	
	
	public static final String INITIALIZE_INTENT = "es.chenton.wallpaper.INITIALIZED";

	private void SendBroadcast(Boolean result, String Msg){
	                Intent i = new Intent();
	                i.setAction(INITIALIZE_INTENT);
	                i.putExtra("result", result);
	                i.putExtra("message", Msg);
	                this.sendBroadcast(i);
	}
	

	
	public static final String PREFS_NAME = "WallPaperChangerPrefs";
	private final IBinder mBinder = new WallPaperChangerServiceBinder();
	
	List<String> photoUrls;
	 PicasaIfz picasa;
	 int photoIndex;
	 
	 String user;
	 String password;

	 boolean restartPending = false;
	 boolean calculateUrls = false;
	 int secondsToChangeBackGround;
	 
	 Collection<String> disallowedAlbumsIds;
	 
	 String LOG_CAT = "WPPCHANGERSVC";
	 
	 boolean initialized = false;
	 
	 Context appContext;
	 
	 Map<String, String> albumIds = new HashMap();
	 
	 private void calculatePhotoUrls() {
		 

	      for (String albumId : albumIds.keySet()) {

	    	  if (!disallowedAlbumsIds.contains(albumId)) {
	    		  List<String> albumPhotos = picasa.getAlbumPhotoUrls(albumId);
	    		  photoUrls.addAll(albumPhotos);
	    	  }
	      	
	      }

	 }
	 
	 private void initialize() {
		
		 if (user.isEmpty()||password.isEmpty()) {
			 Log.d(LOG_CAT, user.isEmpty()+ " "+password.isEmpty());
			 return;
		 }

	     	if (albumIds == null) {
	     		albumIds = new HashMap();
	     	}
	     	
		   photoUrls = new ArrayList<String>();
		   
		picasa = (PicasaIfz)new PicasaWS(user,password, this.getApplicationContext() );
     	
		if (picasa.isAttached()) {
			albumIds = picasa.getAlbumsIdsByAlbumTitles();	
		}
		
     	
     	
     	this.calculatePhotoUrls();     	
      photoIndex = 0;

      
      if (!photoUrls.isEmpty())
    	  
    	  initialized = true;
		 
      
      if (initialized) {
 
    	  /*
    	  for (int i=mClients.size()-1; i>=0; i--) {
              try {
                  mClients.get(i).send(Message.obtain(null,
                		  WallPaperChangerService.MSG_INITIALIZE_COMPLETE));
              } catch (RemoteException e) {
                  // The client is dead.  Remove it from the list;
                  // we are going through the list from back to front
                  // so this is safe to do inside the loop.
                  mClients.remove(i);
              }
      
          */}
      this.SendBroadcast(true, "Initialized");
	 
      
      
	 }
	 
	 
	   @Override
	    public void onCreate() {

		   
		   Log.d(LOG_CAT,"onCreate");
		   
		   this.appContext = this.getApplicationContext();
		   
		   this.readSettings();
		   
		   
	   }
	
	
	   public void saveSettings() {
		   SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
	       
		   settings.edit().putString("user", user);
		   settings.edit().putString("password", password);
		   settings.edit().putStringSet("disalloweddAlbums", new HashSet<String>(this.disallowedAlbumsIds));
		   
	   }
	   
	   public void readSettings() {
		   
		// Restore preferences
	       SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
	       user = settings.getString("user", "");
	       password = settings.getString("password", "");
	       disallowedAlbumsIds = settings.getStringSet("disallowedAlbums", new HashSet<String>());
	       
	   }
	   
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

	
		   Log.d(LOG_CAT,"onStart");
		//Check network available
		
           new CycleWallPaper().execute();
		   
		return Service.START_NOT_STICKY;	
		
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return mBinder;
	}

	public class WallPaperChangerServiceBinder extends Binder {
		WallPaperChangerService getService() {
			return WallPaperChangerService.this;
		}
	}


    private class CycleWallPaper extends AsyncTask {
    	@Override
        protected Object doInBackground(Object... arg0) {

    		
    		Random random = new Random();
    		
    		
    		
    		if (!initialized) {
    			initialize();
    			
    			if (!initialized) {
    				return Service.START_NOT_STICKY;
    			}
    		} else if (restartPending) {
    			initialize();
    			restartPending = false;
    			calculateUrls = false;
    		} else if (calculateUrls) {
    			calculatePhotoUrls();
    			calculateUrls = false;
    			
    		}
    		
    		photoIndex = random.nextInt(photoUrls.size()-1);
    		
    		WallPaperService service  =  new WallPaperService(appContext);
    		
    		service.setWallPaper(photoUrls.get(photoIndex));

    		

            return null;
    	}
    	
    }
    

	
}
