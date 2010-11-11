package com.softwareprojects.androidtasks;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class TaskAlarmReceiver extends BroadcastReceiver {

	private final static String TAG = TaskAlarmReceiver.class.getSimpleName();
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i(TAG, "TaskAlarmReceiver is receiving ...");
		Log.i(TAG, "Intent received carries data: " + intent.getData().toString());
		
		long taskId = intent.getExtras().getLong(Constants.ALARM_TASK_ID);
		Log.i(TAG, "Intent received was constructed for task " + taskId);
		
		String taskDescription = intent.getExtras().getString(Constants.ALARM_TASK_DESCRIPTION);
		Log.i(TAG, "Intent received was constructed for task " + taskDescription);		
	}
}
