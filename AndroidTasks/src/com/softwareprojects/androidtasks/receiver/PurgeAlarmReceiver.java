package com.softwareprojects.androidtasks.receiver;

import roboguice.receiver.RoboBroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.softwareprojects.androidtasks.Constants;

public class PurgeAlarmReceiver extends RoboBroadcastReceiver {

	private final String TAG = PurgeAlarmReceiver.class.getSimpleName();
	
	@Override
	public void onReceive(Context context, Intent intent) {
		
		Log.i(TAG, "Received sync broadcast");
		
		Intent serviceIntent = new Intent("com.softwareprojects.androidtasks.PURGE", 
				Uri.parse(Constants.ANDROIDTASK_TASK_PURGE));

		context.startService(serviceIntent);
		
		Log.i(TAG, "Purge service has been started");
	}
}
