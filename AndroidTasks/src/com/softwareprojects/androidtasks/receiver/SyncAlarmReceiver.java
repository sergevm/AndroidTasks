package com.softwareprojects.androidtasks.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.softwareprojects.androidtasks.Constants;


public class SyncAlarmReceiver extends BroadcastReceiver {
	
	final static String TAG = SyncAlarmReceiver.class.getSimpleName();
	
	@Override
	public void onReceive(Context context, Intent intent) {
		
		Log.v(TAG, "Receiving sync broadcast");
		
		Intent serviceIntent = new Intent("com.softwareprojects.androidtasks.SYNC", 
				Uri.parse(Constants.ANDROIDTASK_TASK_SYNC));

		context.startService(serviceIntent);	
	}

}
