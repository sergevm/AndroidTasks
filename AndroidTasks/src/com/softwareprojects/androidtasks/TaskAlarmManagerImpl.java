package com.softwareprojects.androidtasks;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.softwareprojects.androidtasks.domain.NotificationSource;
import com.softwareprojects.androidtasks.domain.Task;
import com.softwareprojects.androidtasks.domain.TaskAlarmManager;

public class TaskAlarmManagerImpl implements TaskAlarmManager {

	final Context context;
	final AlarmManager alarmManager;
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DATETIME_FORMAT_STRING);
	private static final String COM_SOFTWAREPROJECTS_ANDROIDTASKS_ALARM_ACTION = "com.softwareprojects.androidtasks.ALARM";
		
	private static final String TAG = TaskAlarmManagerImpl.class.getSimpleName();

	public TaskAlarmManagerImpl(Context context) {
		this.context = context;
		this.alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
	}

	@Override
	public void snoozeAlarm(final Task task, int snoozeTimeInMinutes, NotificationSource source) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MINUTE, snoozeTimeInMinutes);
		
		setAlarm(task, calendar.getTime(), source);
}

	@Override
	public void setAlarm(final Task task, Date date, NotificationSource source) {
		Log.i(TAG, "Updating alarm for task: alarm time is " + dateFormat.format(date));

		Uri uri = Uri.parse(Constants.ANDROIDTASK_TASK_ALARM_URI + task.getId());

		Log.i(TAG, "Intent construction for " + uri.toString());

		// Create the intent that will be handed to the broadcast receiver ...
		Intent intent = new Intent(COM_SOFTWAREPROJECTS_ANDROIDTASKS_ALARM_ACTION, uri, context, TaskAlarmReceiver.class);
		intent.putExtra(Constants.ALARM_TASK_DESCRIPTION, task.getDescription());
		intent.putExtra(Constants.ALARM_TASK_ID, task.getId());
		intent.putExtra(Constants.ALARM_SOURCE, source.toString());

		// Create the pending intent ...
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

		// Set the alarm ...
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
	}
}
