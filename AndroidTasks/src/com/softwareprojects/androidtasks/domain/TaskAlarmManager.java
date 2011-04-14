package com.softwareprojects.androidtasks.domain;

import java.util.Calendar;
import java.util.Date;

public interface TaskAlarmManager {
	
	/**
	 * Creates an alarm that should trigger a notification to the user that the first reminder date for the 
	 * task has been reached. This method sets the <emp>first</emp> reminder notification, after the task has 
	 * been created or after the task has been updated. Consequent notifications should be set using the 
	 * setNextReminderNotificationAlarm method.
	 * 
	 * @param task The task whose target date is reached
	 */
	public abstract void resetReminderNotificationAlarm(final Task task);
	
	/**
	 * Sets an alarm that should trigger a secondar notification related to a reminder for the task. 
	 * Snoozing means that a reminder notification was triggered earlier, and the user decided to 
	 * snooze that reminder for a particular time. In other words, it is an alarm that should trigger 
	 * a notification that is not tied to e.g. the target date or the reminder date of the task.
	 * 
	 * @param task The snoozed task, for which a reminder is being snoozed.
	 * @param date
	 * @param source
	 */
	public abstract void snoozeAlarm(final Task task, final Date date, NotificationSource source);
	
	/**
	 * Sets an alarm for the specified task, that should trigger a notification on the reminder date 
	 * that has been specified on that task. This method should be used when the task itself has not 
	 * been updated.
	 * 
	 * @param task
	 */
	public abstract void setNextReminderNotificationAlarm(final Task task);
	
	/**
	 * Removes any alarms that have been set to trigger a notification for the task.
	 * 
	 * @param task The task for which to remove the notification alarm.
	 */
	public abstract void removeReminderNotificationAlarm(final Task task);
	
	/**
	 * Sets an alarm that should trigger instantiation of a next instance of a recurrent task
	 * 
	 * @param task The recurrent task for which a next instance should be created at the time the 
	 * alarm goes off.
	 * 
	 * @param initializationDate The time that the alarm should go off.
	 */
	public abstract void setInstantiateRecurrentTaskAlarm(final Task task, final Date initializationDate);
	
	/**
	 * Sets an alarm that signals a request to execute a purge of completed tasks
	 * 
	 * @param date The time that the purge should be executed.
	 */
	public abstract void setPurgeAlarm(final Calendar date);
	
	/**
	 * Cancels an alarm that has been set to signal the request for a purge execution
	 */
	public abstract void removePurgeAlarm();
	
	/**
	 * Sets an alarm that signals a request to execute synchronization of the tasks with an external 
	 * party.
	 * 
	 * @param date
	 */
	public abstract void setSynchronizationAlarm(final Calendar date);
	
	/**
	 * Handles completion of a task. Removes any alarms that have been set for this task, except 
	 * for the alarm that handles instantiation of a next instance of a recurrent task.
	 * 	
	 * @param task The task that has been completed.
	 */
	public abstract void complete(final Task task);

	/**
	 * Removes all alarms that are associated with the task, including an alarm that would trigger the 
	 * instantiation of a next instance if this is a recurrent task.
	 * 
	 * @param task The task for which to cancel all alarms.
	 */
	public abstract void remove(final Task task);

	/**
	 * Removes existing alarm associated with the task, that would trigger instantiation of the 
	 * next instance of that recurrent task.
	 * 
	 * @param task The recurrent task for which to cancel the triggering of instantiation of the 
	 * next instance
	 */
	public abstract void removeInstantiateRecurrentTaskAlarm(Task task);
}
