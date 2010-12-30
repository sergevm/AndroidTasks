package com.softwareprojects.androidtasks;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class PurgeAlarmReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Intent serviceIntent = new Intent("com.softwareprojects.androidtasks.PURGE", 
				Uri.parse(Constants.ANDROIDTASK_TASK_PURGE));

		context.startService(serviceIntent);
	}
}
