package es.chent;

import java.io.IOException;
import java.util.HashMap;

import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class WallPaperService {

	String LOG_CAT = "[WALLPPSRV]";

	Context appContext;

	public WallPaperService(Context appContext) {
		this.appContext = appContext;
	}

	public void setWallPaper(String url) {

		Log.d(LOG_CAT, "Setting wallpaper for: " + url);

		try {
			GetClient getClient = new GetClient(url, new HashMap());

			WallpaperManager wallpaperManager = WallpaperManager
					.getInstance(appContext);

			
			final int fullWidth = wallpaperManager.getDesiredMinimumWidth();
			final int fullHeight = wallpaperManager.getDesiredMinimumHeight();
			Log.d(LOG_CAT, "Wallpaper reports: " + fullWidth+"x"+fullHeight);
			

			Bitmap myBitmap = BitmapFactory.decodeStream(getClient.getInputStream());

			Log.d(LOG_CAT, "Bitmap reports: " + myBitmap.getWidth()+"x"+myBitmap.getHeight());
			
			if (myBitmap.getWidth()<fullWidth && myBitmap.getHeight()<fullHeight) {
				Log.d(LOG_CAT, "Add padding");
	            int xPadding = Math.max(0, wallpaperManager.getDesiredMinimumWidth() - myBitmap.getWidth()) / 2;
	            int yPadding = Math.max(0, wallpaperManager.getDesiredMinimumHeight() - myBitmap.getHeight()) / 2;
	            Bitmap paddedWallpaper = Bitmap.createBitmap(wallpaperManager.getDesiredMinimumWidth(), wallpaperManager.getDesiredMinimumHeight(), Bitmap.Config.ARGB_8888);
	            int[] pixels = new int[myBitmap.getWidth() * myBitmap.getHeight()];
	            myBitmap.getPixels(pixels, 0, myBitmap.getWidth(), 0, 0, myBitmap.getWidth(), myBitmap.getHeight());
	            paddedWallpaper.setPixels(pixels, 0, myBitmap.getWidth(), xPadding, yPadding, myBitmap.getWidth(), myBitmap.getHeight());

	            wallpaperManager.setBitmap(paddedWallpaper);
				
				
			} else {
				Bitmap bitmapResized = Bitmap.createScaledBitmap(myBitmap,
						fullWidth, fullHeight, true);

				wallpaperManager.setBitmap(bitmapResized);
	
			}
				
				
			
			
			
			
					
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
		
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
