package com.softwareprojects.androidtasks.domain;

import java.util.Calendar;
import java.util.Date;

public class TaskScheduler {

	private final static String TAG = TaskScheduler.class.getSimpleName();

	private TaskAlarmManager alarms;
	private TaskDateProvider dates;
	private TaskRepository repository;
	private RecurrenceCalculations recurrences;
	private ReminderCalculations reminders;
	private ILog log;

	public TaskScheduler(ReminderCalculations reminders, RecurrenceCalculations recurrences, TaskAlarmManager alarms,
			TaskDateProvider dates, TaskRepository repository, ILog log) {

		this.log = log;
		this.dates = dates;
		this.alarms = alarms;
		this.reminders = reminders;
		this.recurrences = recurrences;

		this.repository = repository;
	}

	public void complete(Task task) {

		log.v(TAG, "Completing task with id " + task.getId());

		task.complete();
		alarms.complete(task);
		repository.update(task);

		log.v(TAG, "Task with id " + task.getId() + " is completed.");
	}

	public void initializeNextOccurrence(final Task task) {

		Task next = repository.findNextOccurrenceOf(task);

		if(next != null) {
			log.v(TAG, "Task with id " + task.getId() + 
					" has next occurrence with id " + next.getId()); 
		}
		else {
			log.v(TAG, "Task with id " + task.getId() + 
			" does not have previously created next occurence");
		}

		if(next == null) { 
			next = createOrScheduleNextOccurrence(task);
		}

		if(next == null) {
			return;
		}

		next.initializeReminders(reminders, dates);

		if(next.getReminderDate() != null) {
			if(next.getTargetDate() == null) {
				alarms.setReminder(next);
			}
			else {
				alarms.setTarget(next, next.getReminderDate());				
			}

			alarms.setRecurrent(next, next.getReminderDate());
		}

		repository.update(next);
	}

	public void snooze(final Task task, int minutes, NotificationSource notificationType) {

		Date snoozedTime = task.snooze(dates, minutes);
		if(snoozedTime == null) {
			log.v(TAG, "Task returned snoozed time null, so that time falls after an upcoming reminder time.");
			return;
		}

		switch (notificationType) {
		case ALARMSOURCE_REMINDERDATE:
		case ALARMSOURCE_SNOOZE_REMINDERDATE:

			alarms.snoozeAlarm(task, snoozedTime, NotificationSource.ALARMSOURCE_SNOOZE_REMINDERDATE);

			break;
		case ALARMSOURCE_TARGETDATE:
		case ALARMSOURCE_SNOOZE_TARGETDATE:

			assert task.getTargetDate() != null;
			assert task.getReminderDate() != null;

			alarms.snoozeAlarm(task, snoozedTime, NotificationSource.ALARMSOURCE_SNOOZE_TARGETDATE);
			break;
		}

		repository.update(task);
	}

	public void schedule(final Task task) {

		if (task.getId() == 0) {
			repository.insert(task);
		}

		if(task.isCompleted() == false) {

			task.initializeReminders(reminders, dates);

			if (task.getReminderDate() != null) {
				alarms.setTarget(task, task.getReminderDate());
			}
			else {
				alarms.clearReminder(task);
			}

			initializeNextOccurrence(task);
		}

		repository.update(task);
	}

	public void updateReminder(final Task task) {

		task.updateReminder(reminders, dates);

		if (task.getReminderDate() != null) {
			alarms.setReminder(task);
		}

		repository.update(task);
	}

	public void delete(final Task task) {
		log.v(TAG, String.format("Deleting task with id %d", task.getId()));
		
		alarms.remove(task);
		repository.delete(task);
	}

	public void purge(int weeks) {
		log.v(TAG, "Purging completed tasks that are older than " + weeks + " weeks");
		
		if(weeks <= 0) {
			log.v(TAG, "Cancelling purging of old tasks");
			alarms.cancelPurge();
			return;
		}
		
		repository.purge(weeks);
		
		log.v(TAG, "Completed tasks have been purged");
		
		Calendar calendar = dates.getNow();
		calendar.add(Calendar.DATE, 1);
		alarms.schedulePurge(calendar);
		
		log.v(TAG, "Next purge has been scheduled for " + TaskDateFormatter.format(calendar.getTime()));
	}

	private void scheduleNextOccurrenceCreation(final Task task) {
		if(task.hasFutureTargetDate(dates)) {
			alarms.setRecurrent(task, task.getTargetDate());
		}
	}

	private Task createOrScheduleNextOccurrence(final Task task) {

		Task next = task.createNextOccurrence(recurrences, dates);

		if(next != null) {
			repository.insert(next);
			task.setNextOccurrenceId(next.getId());
			repository.update(task);

			log.v(TAG, "A new occurrence with id " + next.getId() + 
					" was created for task with id " + task.getId());
		}
		else {
			scheduleNextOccurrenceCreation(task);
		}

		return next;
	}
}
