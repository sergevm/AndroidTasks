package com.softwareprojects.androidtasks;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.softwareprojects.androidtasks.domain.Task;

public class TaskAlarmManager {

	final Context context;
	final AlarmManager alarmManager;
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DATETIME_FORMAT_STRING);
	private static final String COM_SOFTWAREPROJECTS_ANDROIDTASKS_ALARM_ACTION = "com.softwareprojects.androidtasks.ALARM";

	private static final String TAG = TaskAlarmManager.class.getSimpleName();

	public TaskAlarmManager(Context context) {
		this.context = context;
		this.alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
	}

	public void setAlarm(final Task task) {
		if(task.completed == false && task.targetDate != null) {			
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(task.targetDate);		

			setAlarm(task, calendar);
		}
	}

	public void snoozeAlarm(final Task task, Integer snoozeTimeInMinutes) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MINUTE, snoozeTimeInMinutes);

		setAlarm(task, calendar);
	}

	private void setAlarm(final Task task, Calendar calendar) {
		Log.i(TAG, "Updating alarm for task: alarm time is " + dateFormat.format(calendar.getTime()));

		Uri uri = Uri.parse(Constants.ANDROIDTASK_TASK_ALARM_URI + task.id);

		Log.i(TAG, "Intent construction for " + uri.toString());

		// Create the intent that will be handed to the broadcast receiver ...
		Intent intent = new Intent(COM_SOFTWAREPROJECTS_ANDROIDTASKS_ALARM_ACTION, uri, context, TaskAlarmReceiver.class);
		intent.putExtra(Constants.ALARM_TASK_DESCRIPTION, task.description);
		intent.putExtra(Constants.ALARM_TASK_ID, task.id);

		// Create the pending intent ...
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

		// Set the alarm ...
		alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
	}
}
