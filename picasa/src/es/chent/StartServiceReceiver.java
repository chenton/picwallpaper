package es.chent;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class StartServiceReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		Intent service = new Intent(context, WallPaperChangerService.class);
		context.startService(service);
		
	}

	
	
}
