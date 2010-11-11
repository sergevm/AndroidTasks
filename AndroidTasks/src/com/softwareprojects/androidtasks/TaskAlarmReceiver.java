package com.softwareprojects.androidtasks;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

public class TaskAlarmReceiver extends BroadcastReceiver {

	private final static String TAG = TaskAlarmReceiver.class.getSimpleName();
	final static Integer REQUEST_CODE = 0;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i(TAG, "TaskAlarmReceiver is receiving ...");
		Log.i(TAG, "Intent received carries data: " + intent.getData().toString());
		
		long taskId = intent.getExtras().getLong(Constants.ALARM_TASK_ID);
		Log.i(TAG, "Intent received was constructed for task " + taskId);
		
		String taskDescription = intent.getExtras().getString(Constants.ALARM_TASK_DESCRIPTION);
		Log.i(TAG, "Intent received was constructed for task " + taskDescription);		
		
		NotificationManager nm = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification();
		notification.tickerText = taskDescription;
		notification.icon = R.drawable.icon;
		
		// Make the notification disappear after the user clicked on it ...
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		
		nm.cancel((int) taskId);
		
		Intent notificationIntent = new Intent(context, TaskNotification.class);
		notificationIntent.putExtra(Constants.ALARM_TASK_ID, taskId);
		
		// We need to set this data, such that the filterEquals() implementation recognizes the different 
		// intents for the different task id's ...
		notificationIntent.setData(Uri.parse(Constants.ANDROIDTASK_TASK_ALARM_URI + taskId));
		
		PendingIntent pendingIntent = PendingIntent.getActivity(context, REQUEST_CODE, notificationIntent, Intent.FLAG_ACTIVITY_NEW_TASK);
		notification.setLatestEventInfo(context, context.getString(R.string.task_due_notification), taskDescription, pendingIntent);
		
		nm.notify((int)taskId, notification);
	}
}
