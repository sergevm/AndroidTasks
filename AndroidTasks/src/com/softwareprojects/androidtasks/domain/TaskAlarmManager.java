package com.softwareprojects.androidtasks.domain;

import java.util.Calendar;
import java.util.Date;

public interface TaskAlarmManager {
	public abstract void setTarget(final Task task, final Date date);
	public abstract void snoozeAlarm(final Task task, final Date date, NotificationSource source);
	public abstract void setReminder(final Task task);
	public abstract void clearReminder(final Task task);
	public abstract void complete(final Task task);
	public abstract void setRecurrent(final Task task, final Date initializationDate);
	public abstract void schedulePurge(final Calendar date);
	public abstract void cancelPurge();
	public abstract void remove(final Task task);
}
