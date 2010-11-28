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
import com.softwareprojects.androidtasks.domain.TaskDateProvider;

public class TaskAlarmManagerImpl implements TaskAlarmManager {

	private final Context context;
	private final AlarmManager alarmManager;
	private final TaskDateProvider dates;
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DATETIME_FORMAT_STRING);
	private static final String COM_SOFTWAREPROJECTS_ANDROIDTASKS_ALARM_ACTION = "com.softwareprojects.androidtasks.ALARM";
		
	private static final String TAG = TaskAlarmManagerImpl.class.getSimpleName();

	public TaskAlarmManagerImpl(Context context, TaskDateProvider dates) {
		this.context = context;
		this.dates = dates;
		this.alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
	}

	@Override
	public void snoozeAlarm(final Task task, int snoozeTimeInMinutes, NotificationSource source) {
		Log.i(TAG, "Snoozing task with id " + task.getId() + " for " + snoozeTimeInMinutes + " minutes");
		
		Calendar calendar = dates.getNow();
		calendar.add(Calendar.MINUTE, snoozeTimeInMinutes);
		
		if(calendar.getTime().before(task.getReminderDate()) == false) {
			Log.i(TAG, "Snoozing task is cancelled because alarm would be later than reminder date");
			return;
		}

		Uri uri = Uri.parse(Constants.ANDROIDTASK_TASK_CURRENT_ALARM_URI + task.getId());		
		setAlarm(task, uri, calendar.getTime(), source);
}

	@Override
	public void setAlarm(final Task task, Date date, NotificationSource source) {

		// Remove any existing reminder type alarm ...
		removeAlarm(task, Uri.parse(Constants.ANDROIDTASK_TASK_NEXT_REMINDER_URI + task.getId()));

		// Set a new "regular" alarm
		Log.i(TAG, "Setting alarm for task with id " + task.getId() + "on " + dateFormat.format(date));

		Uri uri =  Uri.parse(Constants.ANDROIDTASK_TASK_CURRENT_ALARM_URI + task.getId());
		setAlarm(task, uri, date, source);
	}
	
	@Override
	public void complete(final Task task) {
		Log.i(TAG, "Completing task with id " + task.getId());
				
		removeAlarm(task, Uri.parse(Constants.ANDROIDTASK_TASK_CURRENT_ALARM_URI + task.getId()));
		removeAlarm(task, Uri.parse(Constants.ANDROIDTASK_TASK_NEXT_REMINDER_URI + task.getId()));
	}

	@Override
	public void setReminder(final Task task) {
		
		// Remove any pending intent used to create an alarm
		removeAlarm(task, Uri.parse(Constants.ANDROIDTASK_TASK_CURRENT_ALARM_URI + task.getId()));

		// Set a "reminder" alarm
		Log.i(TAG, "Setting reminder for task with id " + task.getId() + " on " + dateFormat.format(task.getReminderDate()));

		Uri uri =  Uri.parse(Constants.ANDROIDTASK_TASK_NEXT_REMINDER_URI + task.getId());
		setAlarm(task, uri, task.getReminderDate(), NotificationSource.ALARMSOURCE_REMINDERDATE);
	}
	
	private void setAlarm(final Task task, Uri uri, Date date, NotificationSource source) {

		// Create the intent that will be handed to the broadcast receiver ...
		Intent intent = new Intent(COM_SOFTWAREPROJECTS_ANDROIDTASKS_ALARM_ACTION, uri, context, TaskAlarmReceiver.class);
		intent.putExtra(Constants.ALARM_TASK_ID, task.getId());
		intent.putExtra(Constants.ALARM_SOURCE, source.toString());
		intent.putExtra(Constants.ALARM_TASK_DESCRIPTION, task.getDescription());

		// Create the pending intent ...
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

		// Set the alarm ...
		Calendar calendar = dates.getNow();
		calendar.setTime(date);
		alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
	}
	
	private void removeAlarm(final Task task, final Uri uri) {
		Log.i(TAG, "Removing alarm for task with id " + task.getId() + " (" + uri.toString() + ")");

		Intent intent = new Intent(COM_SOFTWAREPROJECTS_ANDROIDTASKS_ALARM_ACTION, uri, context, TaskAlarmReceiver.class);
		PendingIntent existingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_NO_CREATE);
		
		if(existingIntent == null) {
			Log.i(TAG, "No alarm to remove on task with id " + task.getId());
		}
		else {
			Log.i(TAG, "Pending intent found for alarm on task with id " + task.getId());
			alarmManager.cancel(existingIntent);
			existingIntent.cancel();			
		}
	}
}
