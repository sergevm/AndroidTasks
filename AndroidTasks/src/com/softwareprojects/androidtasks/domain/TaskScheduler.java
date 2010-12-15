package com.softwareprojects.androidtasks.domain;

import android.util.Log;

public class TaskScheduler {

	private final static String TAG = TaskScheduler.class.getSimpleName();

	private TaskAlarmManager alarms;
	private TaskDateProvider dates;
	private TaskRepository repository;
	private RecurrenceCalculations recurrences;
	private ReminderCalculations reminders;

	public TaskScheduler(ReminderCalculations reminders, RecurrenceCalculations recurrences, TaskAlarmManager alarms,
			TaskDateProvider dates, TaskRepository repository) {

		this.dates = dates;
		this.alarms = alarms;
		this.reminders = reminders;
		this.recurrences = recurrences;

		this.repository = repository;
	}

	public void complete(Task task) {

		Log.v(TAG, "Completing task with id " + task.getId());

		task.complete();
		alarms.complete(task);
		repository.update(task);

		Log.v(TAG, "Task with id " + task.getId() + " is completed.");
	}

	public void createNextOccurrence(final Task task) {

		Task next = repository.getNextOccurrenceOf(task);
		
		if(next != null) {
			Log.v(TAG, "Task with id " + task.getId() + 
					" has next occurrence with id " + next.getId()); 
		}
		else {
			Log.v(TAG, "Task with id " + task.getId() + 
					" does not have previously created next occurence");
		}
		
		if(next == null) {
			next = task.createNextOccurrence(recurrences, dates);
			if(next == null) {
				return;
			} 
			else {
				repository.insert(next);
				task.setNextOccurrenceId(next.getId());
				repository.update(task);
				
				Log.v(TAG, "A new occurrence with id " + next.getId() + 
						" was created for task with id " + task.getId());
			}
		}

		// Reminders
		NotificationSource source = next.initializeReminders(reminders, dates);
		if(next.getReminderDate() != null)
		{
			alarms.setAlarm(next, next.getReminderDate(), source);
		}
		
		// Recurrence
		if(next.getReminderDate() != null) {
			alarms.setRecurrentTask(next, next.getReminderDate());
		}
		
		repository.update(next);
	}

	public void snooze(final Task task, int minutes, NotificationSource notificationType) {

		switch (notificationType) {
		case ALARMSOURCE_REMINDERDATE:
		case ALARMSOURCE_SNOOZE_REMINDERDATE:

			alarms.snoozeAlarm(task, dates.getNow().getTime(), minutes,
					NotificationSource.ALARMSOURCE_SNOOZE_REMINDERDATE);

			break;
		case ALARMSOURCE_TARGETDATE:
		case ALARMSOURCE_SNOOZE_TARGETDATE:

			assert task.getTargetDate() != null;
			assert task.getReminderDate() != null;

			alarms.snoozeAlarm(task, dates.getNow().getTime(), minutes,
					NotificationSource.ALARMSOURCE_SNOOZE_TARGETDATE);
			break;
		}

		task.setSnoozeCount(task.getSnoozeCount() + 1);

		repository.update(task);
	}

	public void schedule(final Task task) {

		if (task.getId() == 0) {
			repository.insert(task);
		} else {
			repository.update(task);
		}

		if (task.isCompleted() == false) {
			NotificationSource source = task.initializeReminders(reminders, dates);

			if (task.getReminderDate() != null) {
				alarms.setAlarm(task, task.getReminderDate(), source);
			}

			createNextOccurrence(task);
		}
	}

	public void updateReminder(final Task task) {

		task.updateReminder(reminders, dates);

		if (task.getReminderDate() != null) {
			alarms.setReminder(task);
		}

		repository.update(task);
	}
}
