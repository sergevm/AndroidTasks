package com.softwareprojects.androidtasks;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.softwareprojects.androidtasks.domain.NotificationSource;

public class TaskAlarmReceiver extends BroadcastReceiver {

	private final static String TAG = TaskAlarmReceiver.class.getSimpleName();
	final static Integer REQUEST_CODE = 0;

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i(TAG, "Intent received has data: " + intent.getData().toString());

		long taskId = intent.getExtras().getLong(Constants.ALARM_TASK_ID);
		Log.i(TAG, "Intent received handles task with id " + taskId);
		
		String taskDescription = intent.getExtras().getString(Constants.ALARM_TASK_DESCRIPTION);
		
		NotificationSource notificationSource = NotificationSource
				.valueOf(intent.getExtras().getString(Constants.ALARM_SOURCE));
		Log.i(TAG, "Intent received handles notification source " + notificationSource);
		
		Uri uri = Uri.parse(Constants.ANDROIDTASK_TASK_CURRENT_ALARM_URI + taskId);
		PendingIntent pendingIntent = createPendingIntent(context, uri, taskId, notificationSource);
		
		createNotification(context, pendingIntent, (int) taskId, taskDescription);
	}
	
	private PendingIntent createPendingIntent(Context context, Uri uri, long id, NotificationSource source) {
		Intent notificationIntent = new Intent(context, TaskNotification.class);
		
		notificationIntent.putExtra(Constants.ALARM_TASK_ID, id);
		notificationIntent.putExtra(Constants.ALARM_SOURCE,	source.toString());

		notificationIntent.setData(uri);

		PendingIntent pendingIntent = PendingIntent.getActivity(context, 
				REQUEST_CODE, notificationIntent, Intent.FLAG_ACTIVITY_NEW_TASK);
		
		return pendingIntent;
	}
	
	private void createNotification(Context context, PendingIntent pendingIntent, int id, String description) {

		NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

		Notification notification = new Notification();
		notification.tickerText = description;
		notification.icon = R.drawable.exclamation;

		notification.flags |= Notification.FLAG_AUTO_CANCEL	| Notification.DEFAULT_VIBRATE;

		notification.setLatestEventInfo(context, context.getString(R.string.task_due_notification),
				description, pendingIntent);

		nm.notify(id, notification);
	}
}
