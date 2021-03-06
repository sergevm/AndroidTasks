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

import com.google.inject.Inject;
import com.softwareprojects.androidtasks.domain.NotificationSource;
import com.softwareprojects.androidtasks.domain.Task;
import com.softwareprojects.androidtasks.domain.TaskAlarmManager;
import com.softwareprojects.androidtasks.receiver.PurgeAlarmReceiver;
import com.softwareprojects.androidtasks.receiver.SyncAlarmReceiver;
import com.softwareprojects.androidtasks.receiver.TaskAlarmReceiver;

public class AndroidTaskAlarmManager implements TaskAlarmManager {

	private final Context context;
	private final AlarmManager alarmManager;
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DATETIME_FORMAT_STRING);
	private static final String COM_SOFTWAREPROJECTS_ANDROIDTASKS_ALARM_ACTION = "com.softwareprojects.androidtasks.ALARM";

	private static final String TAG = AndroidTaskAlarmManager.class.getSimpleName();

	@Inject
	public AndroidTaskAlarmManager(Context context) {
		this.context = context;
		this.alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
	}

	@Override
	public void snoozeAlarm(final Task task, final Date snoozedDate, NotificationSource source) {
		Log.i(TAG, "Snoozing task with id " + task.getId() + " until " + dateFormat.format(snoozedDate));

		Uri uri = Uri.parse(Constants.ANDROIDTASK_TASK_CURRENT_ALARM_URI + task.getId());
		setAlarm(task, uri, snoozedDate, source);
	}

	@Override
	public void setNextReminderNotificationAlarm(final Task task) {

		removeAlarm(task, Uri.parse(Constants.ANDROIDTASK_TASK_NEXT_REMINDER_URI + task.getId()));
		removeAlarm(task, Uri.parse(Constants.ANDROIDTASK_TASK_CURRENT_ALARM_URI + task.getId()));

		Log.i(TAG,
				String.format("Setting reminder for task with id %d on %s", task.getId(),
						dateFormat.format(task.getReminderDate())));

		Uri uri = Uri.parse(Constants.ANDROIDTASK_TASK_NEXT_REMINDER_URI + task.getId());
		setAlarm(task, uri, task.getReminderDate(), NotificationSource.ALARMSOURCE_REMINDERDATE);
	}

	@Override
	public void complete(final Task task) {

		Log.v(TAG, "Completing task with id " + task.getId());

		removeAlarm(task, Uri.parse(Constants.ANDROIDTASK_TASK_CURRENT_ALARM_URI + task.getId()));
		removeAlarm(task, Uri.parse(Constants.ANDROIDTASK_TASK_NEXT_REMINDER_URI + task.getId()));
	}

	@Override
	public void remove(Task task) {

		Log.v(TAG, String.format("Removing task with id %d", task.getId()));

		removeAlarm(task, Uri.parse(Constants.ANDROIDTASK_TASK_CURRENT_ALARM_URI + task.getId()));
		removeAlarm(task, Uri.parse(Constants.ANDROIDTASK_TASK_NEXT_REMINDER_URI + task.getId()));
		removeAlarm(task, Uri.parse(Constants.ANDROIDTASK_TASK_NEXT_RECURRENCE_URI + task.getId()));
	}

	@Override
	public void setInstantiateRecurrentTaskAlarm(Task task, Date initializationDate) {

		removeInstantiateRecurrentTaskAlarm(task);

		Uri uri = Uri.parse(Constants.ANDROIDTASK_TASK_NEXT_RECURRENCE_URI + task.getId());

		Log.i(TAG,
				String.format("Setting recurrence for task with id %d on %s", task.getId(),
						dateFormat.format(initializationDate)));

		setAlarm(task, uri, initializationDate, NotificationSource.ALARMSOURCE_RECURRENCY);
	}

	@Override
	public void removeInstantiateRecurrentTaskAlarm(Task task) {

		Log.i(TAG,
				String.format("Removing recurrence for task with id %d", task.getId()));
		
		Uri uri = Uri.parse(Constants.ANDROIDTASK_TASK_NEXT_RECURRENCE_URI + task.getId());
		removeAlarm(task, uri);
	}

	@Override
	public void resetReminderNotificationAlarm(final Task task) {
	
		removeAlarm(task, Uri.parse(Constants.ANDROIDTASK_TASK_NEXT_REMINDER_URI + task.getId()));
		removeAlarm(task, Uri.parse(Constants.ANDROIDTASK_TASK_CURRENT_ALARM_URI + task.getId()));
	
		Log.i(TAG,
				String.format("Setting alarm for task with id %d on %s", task.getId(),
						dateFormat.format(task.getReminderDate())));
	
		Uri uri = Uri.parse(Constants.ANDROIDTASK_TASK_CURRENT_ALARM_URI + task.getId());
		setAlarm(task, uri, task.getReminderDate(), NotificationSource.ALARMSOURCE_TARGETDATE);
	}

	@Override
	public void removeReminderNotificationAlarm(Task task) {
		
		removeAlarm(task, Uri.parse(Constants.ANDROIDTASK_TASK_NEXT_REMINDER_URI + task.getId()));
		removeAlarm(task, Uri.parse(Constants.ANDROIDTASK_TASK_CURRENT_ALARM_URI + task.getId()));
}

	@Override
	public void setPurgeAlarm(final Calendar date) {

		Log.v(TAG, String.format("Scheduling a purge on %s", dateFormat.format(date.getTime())));

		PendingIntent pendingIntent = getOrCreatePurgePendingIntent();
		alarmManager.set(AlarmManager.RTC_WAKEUP, date.getTimeInMillis(), pendingIntent);
	}

	@Override
	public void removePurgeAlarm() {

		Log.v(TAG, "Canceling purging");

		PendingIntent pendingIntent = getOrCreatePurgePendingIntent();
		alarmManager.cancel(pendingIntent);
		pendingIntent.cancel();
	}

	@Override
	public void setSynchronizationAlarm(final Calendar date) {
		Log.v(TAG, "Scheduling a sync on " + dateFormat.format(date.getTime()));

		PendingIntent pendingIntent = getOrCreateSyncPendingIntent();
		alarmManager.set(AlarmManager.RTC_WAKEUP, date.getTimeInMillis(), pendingIntent);
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

	private PendingIntent getOrCreatePurgePendingIntent() {

		Intent intent = new Intent(COM_SOFTWAREPROJECTS_ANDROIDTASKS_ALARM_ACTION,
				Uri.parse(Constants.ANDROIDTASK_TASK_PURGE), context, PurgeAlarmReceiver.class);

		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

		return pendingIntent;
	}

	private PendingIntent getOrCreateSyncPendingIntent() {

		Intent intent = new Intent(COM_SOFTWAREPROJECTS_ANDROIDTASKS_ALARM_ACTION,
				Uri.parse(Constants.ANDROIDTASK_TASK_SYNC), context, SyncAlarmReceiver.class);

		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

		return pendingIntent;
	}

	private String createLogMessage(final Task task, final Uri uri, final String action) {
		return action + task.getId() + " (" + uri.toString() + ")";
	}

}
