package com.softwareprojects.androidtasks.receiver;

import roboguice.receiver.RoboBroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.softwareprojects.androidtasks.Constants;


public class SyncAlarmReceiver extends RoboBroadcastReceiver {
	
	final static String TAG = SyncAlarmReceiver.class.getSimpleName();
	
	@Override
	public void onReceive(Context context, Intent intent) {
		
		Log.i(TAG, "Received sync broadcast");
		
		Intent serviceIntent = new Intent("com.softwareprojects.androidtasks.SYNC", 
				Uri.parse(Constants.ANDROIDTASK_TASK_SYNC));

		context.startService(serviceIntent);	
		
		Log.i(TAG, "Toodledo sync service has been started");
	}
}
