package com.softwareprojects.androidtasks;

import java.util.Date;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;

import com.softwareprojects.androidtasks.db.TasksDBHelper;
import com.softwareprojects.androidtasks.domain.NotificationSource;

public class TaskAlarmReceiver extends BroadcastReceiver {

	private final static String TAG = TaskAlarmReceiver.class.getSimpleName();
	final static Integer REQUEST_CODE = 0;
	static TasksDBHelper dbHelper;

	@Override
	public void onReceive(Context context, Intent intent) {

		Log.i(TAG, "Intent received has data: " + intent.getData().toString());

		long taskId = intent.getExtras().getLong(Constants.ALARM_TASK_ID);
		Log.v(TAG, "Intent received handles task with id " + taskId);

		String taskDescription = intent.getExtras().getString(Constants.ALARM_TASK_DESCRIPTION);

		NotificationSource notificationSource = NotificationSource
		.valueOf(intent.getExtras().getString(Constants.ALARM_SOURCE));

		Log.v(TAG, "Intent received handles notification source " + notificationSource);

		if(notificationSource == NotificationSource.ALARMSOURCE_RECURRENCY) {
			Intent serviceIntent = new Intent("com.softwareprojects.androidtasks.TASKRECURRENCE", 
					Uri.parse(Constants.ANDROIDTASK_TASK_NEXT_RECURRENCE_URI + taskId));

			serviceIntent.putExtra(Constants.ALARM_TASK_ID, taskId);
			context.startService(serviceIntent);
		}
		else
		{
			Date alarmDate = (Date)intent.getSerializableExtra(Constants.ALARM_DATE);

			Uri uri = Uri.parse(Constants.ANDROIDTASK_TASK_CURRENT_ALARM_URI + taskId);
			PendingIntent pendingIntent = createPendingIntent(context, uri, taskId, notificationSource, alarmDate);

			createNotification(context, pendingIntent, (int) taskId, taskDescription);
		}
	}

	private PendingIntent createPendingIntent(Context context, Uri uri, long id, NotificationSource source, Date alarmDate) {
		Intent notificationIntent = new Intent(context, TaskNotification.class);

		notificationIntent.putExtra(Constants.ALARM_TASK_ID, id);
		notificationIntent.putExtra(Constants.ALARM_DATE, alarmDate);
		notificationIntent.putExtra(Constants.ALARM_SOURCE,	source.toString());

		notificationIntent.setData(uri);

		PendingIntent pendingIntent = PendingIntent.getActivity(context, 
				REQUEST_CODE, notificationIntent, Intent.FLAG_ACTIVITY_NEW_TASK);

		return pendingIntent;
	}

	private void createNotification(Context context, PendingIntent pendingIntent, int id, String description) {

		NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

		Notification notification = new Notification();
		notification.icon = R.drawable.exclamation;
		notification.tickerText = description + " (" + id + ")";
		
		notification.flags |= Notification.FLAG_AUTO_CANCEL;

		SharedPreferences preferences = context.getSharedPreferences("AndroidTasks", Context.MODE_PRIVATE);
		Boolean vibrate = preferences.getBoolean(Constants.PREFS_VIBRATE_ON_NOTIFICATION, false);

		if(vibrate) {
			notification.defaults |= Notification.DEFAULT_VIBRATE;
		}
		
		notification.setLatestEventInfo(context, context.getString(R.string.task_due_notification),
				description + " (" + id + ")", pendingIntent);

		nm.notify(id, notification);
	}
}
