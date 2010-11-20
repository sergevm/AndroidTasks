package com.softwareprojects.androidtasks;

import com.softwareprojects.androidtasks.domain.NotificationSource;

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
		Log.i(TAG, "Intent received has data: " + intent.getData().toString());
		
		long taskId = intent.getExtras().getLong(Constants.ALARM_TASK_ID);
		Log.i(TAG, "Intent received handles task id " + taskId);
		
		String taskDescription = intent.getExtras().getString(Constants.ALARM_TASK_DESCRIPTION);
		Log.i(TAG, "Intent received handles task " + taskDescription);		
		
		NotificationSource notificationSource = NotificationSource.valueOf(intent.getExtras().getString(Constants.ALARM_SOURCE));
		Log.i(TAG, "Intent received handles notification source " + notificationSource);
		
		NotificationManager nm = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification();
		notification.tickerText = taskDescription;
		notification.icon = R.drawable.exclamation;
		
		// Make the notification disappear after the user clicked on it ...
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		
		nm.cancel((int) taskId);
		
		Intent notificationIntent = new Intent(context, TaskNotification.class);
		notificationIntent.putExtra(Constants.ALARM_TASK_ID, taskId);
		notificationIntent.putExtra(Constants.ALARM_SOURCE, notificationSource.toString());
		
		// We need to set this data, such that the filterEquals() implementation recognizes the different 
		// intents for the different task id's ...
		notificationIntent.setData(Uri.parse(Constants.ANDROIDTASK_TASK_ALARM_URI + taskId));
		
		PendingIntent pendingIntent = PendingIntent.getActivity(context, REQUEST_CODE, notificationIntent, Intent.FLAG_ACTIVITY_NEW_TASK);
		notification.setLatestEventInfo(context, context.getString(R.string.task_due_notification), taskDescription, pendingIntent);
		
		nm.notify((int)taskId, notification);
	}
}
