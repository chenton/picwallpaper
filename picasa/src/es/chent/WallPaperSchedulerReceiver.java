package es.chent;

import java.util.Calendar;
import java.util.List;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

public class WallPaperSchedulerReceiver extends BroadcastReceiver {

	// Restart service every 30 seconds
		private static final long REPEAT_TIME = 1000 * 30;
	
	@Override
	public void onReceive(Context context, Intent intent) {

		
		   Log.d("SCHEDRECV","onReceive");
		AlarmManager service = (AlarmManager)  context
				.getSystemService(Context.ALARM_SERVICE);
		Intent i = new Intent(context, StartServiceReceiver.class);
		PendingIntent pending = PendingIntent.getBroadcast(context, 0, i,
				PendingIntent.FLAG_CANCEL_CURRENT);
		Calendar cal = Calendar.getInstance();
		// Start 30 seconds after boot completed
		cal.add(Calendar.SECOND, 30);
		//
		// Fetch every 30 seconds
		// InexactRepeating allows Android to optimize the energy consumption
		service.setInexactRepeating(AlarmManager.RTC_WAKEUP,
				cal.getTimeInMillis(), REPEAT_TIME, pending);

		// service.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
		// REPEAT_TIME, pending);

		
	}

	
	
}
