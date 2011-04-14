package com.softwareprojects.androidtasks.domain;

import java.util.Calendar;
import java.util.Date;

import android.util.Log;

import com.google.inject.Inject;

public class TaskScheduler {

	private final static String TAG = TaskScheduler.class.getSimpleName();

	private TaskAlarmManager alarms;
	private TaskDateProvider dates;
	private TaskRepository repository;
	private RecurrenceCalculations recurrences;
	private ReminderCalculations reminders;
	private ILog log;

	@Inject
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

		log.v(TAG, String.format("Completing task with id %d", task.getId()));

		task.complete();

		if (task.isRecurrent()) {

			instantiateNextOccurrenceOf(task);
		}

		repository.update(task);

		alarms.complete(task);

		log.v(TAG, String.format("Task with id %d has been completed.", task.getId()));
	}

	public void instantiateNextOccurrenceOf(final Task task) {

		Log.v(TAG, String.format("Create next instance for task %d", task.getId()));

		Task existingNextOccurrence = repository.findNextOccurrenceOf(task);

		if (existingNextOccurrence != null) {

			log.d(TAG, String.format("Next instance of the recurring task %d already exists", task.getId()));
			return;
		}

		Task next = createNextInstanceOf(task);

		if (next != null) {

			log.d(TAG, String.format("Scheduling alarms for task %d that is the next occurrence for task %d",
					task.getId(), next.getId()));

			scheduleAlarmsFor(next);
		}
	}

	public void scheduleRecurrentTaskInstantiationFor(final Task task) {

		if (task.isRecurrent() == false) {

			log.e(TAG, "Should not try to schedule an instantiation of a new instance on a task that is not recurrent");
			return;
		}

		Task next = repository.findNextOccurrenceOf(task);

		if (next != null) {
			log.v(TAG, "Task with id " + task.getId() + " has next occurrence with id " + next.getId());
		} else {
			log.v(TAG, "Task with id " + task.getId() + " does not have previously created next occurence");
		}

		if (next == null) {

			scheduleAlarmsForInstantiationOfNextInstanceOf(task);
		} else {

			scheduleAlarmsFor(next);
		}
	}

	private void removeScheduledTaskInstantiationFor(Task task) {

		alarms.removeInstantiateRecurrentTaskAlarm(task);
	}

	/**
	 * If the task has a future target date, then an instantiation of the next
	 * occurrence of the recurrent task is scheduled. In the other case, no
	 * action is executed.
	 * 
	 * @param task
	 *            The recurrent task for which to schedule instantiation of the
	 *            next occurrence
	 */
	private void scheduleAlarmsForInstantiationOfNextInstanceOf(final Task task) {

		if (task.isRecurrent() == false) {

			Log.e(TAG, "Should not try to schedule an alarm for a new instance on a task that is not recurrent");
			return;
		}

		if (task.hasTargetDateInFuture(dates)) {

			log.d(TAG, String.format("Setting alarm for creation of next instance of recurrent task %d", task.getId()));
			alarms.setInstantiateRecurrentTaskAlarm(task, task.getTargetDate());
		} else {

			log.d(TAG, String.format("Skipping setting alarm for next instance of task %d, because current instances' "
					+ "target date is in the future", task.getId()));
		}
	}

	public void snooze(final Task task, int minutes, NotificationSource notificationType) {

		Date snoozedTime = task.snooze(dates, minutes);

		if (snoozedTime == null) {

			log.v(TAG, "Snooze cancelled, because next reminder time precedes the snooze time.");
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

	/**
	 * @param task
	 */
	public void schedule(final Task task) {

		Date modificationDate = new Date();

		task.setModificationDate(modificationDate);

		if (task.getId() <= 0) {

			task.setCreateDate(modificationDate);
			repository.insert(task);
		}

		if (task.isCompleted() == false) {

			task.initializeReminders(reminders, dates);

			if (task.getReminderDate() != null) {

				alarms.resetReminderNotificationAlarm(task);
			} else {

				alarms.removeReminderNotificationAlarm(task);
			}

			if (task.isRecurrent()) {

				scheduleRecurrentTaskInstantiationFor(task);
			} else {

				removeScheduledTaskInstantiationFor(task);
			}
		}

		repository.update(task);
	}

	public void delete(final Task task) {

		log.v(TAG, String.format("Deleting task with id %d", task.getId()));

		alarms.remove(task);

		task.setDeleted(true);
		task.setModificationDate(new Date());

		repository.update(task);
	}

	public void purge(int weeks) {

		log.v(TAG, String.format("Purging completed tasks that are older than %d weeks", weeks));

		if (weeks <= 0) {
			log.v(TAG, "Cancelling purging of old tasks");
			alarms.removePurgeAlarm();
			return;
		}

		repository.purge(weeks);

		log.v(TAG, "Completed tasks have been purged");

		Calendar calendar = dates.getNow();
		calendar.add(Calendar.DATE, 1);
		alarms.setPurgeAlarm(calendar);

		log.v(TAG, String.format("Next purge has been scheduled for %s", TaskDateFormatter.format(calendar.getTime())));
	}

	/**
	 * Calculate the next reminder time for the task, and create an alarm that
	 * triggers a notification for that reminder time.
	 * 
	 * @param task
	 *            The task for which to set and schedule the next reminder.
	 */
	public void createNextReminderFor(final Task task) {

		task.nextReminder(reminders, dates);

		if (task.getReminderDate() != null) {
			alarms.setNextReminderNotificationAlarm(task);
		}

		repository.update(task);
	}

	/**
	 * If the task is to be reminded, then:
	 * <ul>
	 * <li>If the task has no target date, then a notification is set for the
	 * reminder date</li>
	 * <li>In the other case, a notification for reaching of the target date is
	 * set, but on the reminder date ???</li>
	 * <li>An instantiation request is set for the next instance of a recurrent
	 * task</li>
	 * </ul>
	 * 
	 * @param task
	 */
	private void scheduleAlarmsFor(Task task) {

		task.initializeReminders(reminders, dates);

		if (task.getReminderDate() != null) {

			if (task.getTargetDate() == null) {

				alarms.setNextReminderNotificationAlarm(task);

			} else {

				alarms.resetReminderNotificationAlarm(task);
			}

			alarms.setInstantiateRecurrentTaskAlarm(task, task.getReminderDate());
		}

		repository.update(task);
	}

	/**
	 * Creates the next instance of the specified task, provided it is a
	 * recurrent task. The target date of that next instance is calculated based
	 * on the target date of the current instance, taking into account the
	 * recurrence properties set on the current instance.
	 * 
	 * The next instance is immediately persisted, and set as the next instance
	 * on the current instance.
	 * 
	 * @param task
	 *            The task for which the next instance is created
	 * @return The next instance of the task
	 */
	private Task createNextInstanceOf(final Task task) {

		Task next = task.createNextInstance(recurrences, dates);

		if (next != null) {

			repository.insert(next);
			task.setNextOccurrenceId(next.getId());
			repository.update(task);

			log.v(TAG, "A new occurrence with id " + next.getId() + " was created for task with id " + task.getId());
		}

		return next;
	}
}
