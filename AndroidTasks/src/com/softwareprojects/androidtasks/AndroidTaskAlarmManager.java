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

public class AndroidTaskAlarmManager implements TaskAlarmManager {

	private final Context context;
	private final AlarmManager alarmManager;
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DATETIME_FORMAT_STRING);
	private static final String COM_SOFTWAREPROJECTS_ANDROIDTASKS_ALARM_ACTION = "com.softwareprojects.androidtasks.ALARM";

	private static final String TAG = AndroidTaskAlarmManager.class.getSimpleName();

	public AndroidTaskAlarmManager(Context context) {
		this.context = context;
		this.alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
	}

	@Override
	public void snoozeAlarm(final Task task, final Date snoozedDate, NotificationSource source) {
		Log.i(TAG, "Snoozing task with id " + task.getId() + " until "  + dateFormat.format(snoozedDate));

		Uri uri = Uri.parse(Constants.ANDROIDTASK_TASK_CURRENT_ALARM_URI + task.getId());
		setAlarm(task, uri, snoozedDate, source);
	}

	@Override
	public void setTarget(final Task task, Date date) {

		// Remove any existing reminder type alarm ...
		removeAlarm(task, Uri.parse(Constants.ANDROIDTASK_TASK_NEXT_REMINDER_URI + task.getId()));

		// Set a new "regular" alarm
		Log.i(TAG, "Setting alarm for task with id " + task.getId() + "on " + dateFormat.format(date));

		Uri uri = Uri.parse(Constants.ANDROIDTASK_TASK_CURRENT_ALARM_URI + task.getId());
		setAlarm(task, uri, date, NotificationSource.ALARMSOURCE_TARGETDATE);
	}

	@Override
	public void complete(final Task task) {
		Log.v(TAG, "Completing task with id " + task.getId());

		removeAlarm(task, Uri.parse(Constants.ANDROIDTASK_TASK_CURRENT_ALARM_URI + task.getId()));
		removeAlarm(task, Uri.parse(Constants.ANDROIDTASK_TASK_NEXT_REMINDER_URI + task.getId()));
	}

	@Override
	public void setReminder(final Task task) {

		// Remove any pending intent used to create an alarm
		removeAlarm(task, Uri.parse(Constants.ANDROIDTASK_TASK_CURRENT_ALARM_URI + task.getId()));

		// Set a "reminder" alarm
		Log.i(TAG, "Setting reminder for task with id " + task.getId() + " on "	+ dateFormat.format(task.getReminderDate()));

		Uri uri = Uri.parse(Constants.ANDROIDTASK_TASK_NEXT_REMINDER_URI + task.getId());
		setAlarm(task, uri, task.getReminderDate(), NotificationSource.ALARMSOURCE_REMINDERDATE);
	}

	@Override
	public void setRecurrent(Task task, Date initializationDate) {
		Uri uri = Uri.parse(Constants.ANDROIDTASK_TASK_NEXT_RECURRENCE_URI + task.getId());

		// Remove any pending intent used to create the next instance of a recurrent task
		removeAlarm(task, uri);

		Log.i(TAG, "Setting recurrence for task with id " + task.getId() + " on " + dateFormat.format(initializationDate));
		setAlarm(task, uri, initializationDate, NotificationSource.ALARMSOURCE_RECURRENCY);
	}

	private void setAlarm(final Task task, Uri uri, Date date, NotificationSource source) {

		// Create the intent that will be handed to the broadcast receiver ...
		Intent intent = new Intent(COM_SOFTWAREPROJECTS_ANDROIDTASKS_ALARM_ACTION, uri, context,
				TaskAlarmReceiver.class);
		intent.putExtra(Constants.ALARM_TASK_ID, task.getId());
		intent.putExtra(Constants.ALARM_SOURCE, source.toString());
		intent.putExtra(Constants.ALARM_TASK_DESCRIPTION, task.getDescription());

		Calendar calendar = Calendar.getInstance();

		// Create the pending intent ...
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

		// Set the alarm ...
		calendar.setTime(date);
		alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
	}

	private void removeAlarm(final Task task, final Uri uri) {
		Log.v(TAG, createLogMessage(task, uri, "Removing alarm for task with id "));

		Intent intent = new Intent(COM_SOFTWAREPROJECTS_ANDROIDTASKS_ALARM_ACTION, uri, context,
				TaskAlarmReceiver.class);
		PendingIntent existingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_NO_CREATE);

		if (existingIntent == null) {
			Log.v(TAG, createLogMessage(task, uri, "No alarm to remove on task with id "));
		} else {
			Log.v(TAG, createLogMessage(task, uri, "Pending intent found for alarm on task with id "));
			alarmManager.cancel(existingIntent);
			existingIntent.cancel();
		}
	}

	private String createLogMessage(final Task task, final Uri uri, final String action) {
		return action + task.getId() + " (" + uri.toString() + ")";
	}
}
