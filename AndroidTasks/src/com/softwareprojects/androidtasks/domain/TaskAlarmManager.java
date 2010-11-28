package com.softwareprojects.androidtasks.domain;

import java.util.Date;

public interface TaskAlarmManager {
	public abstract void setAlarm(final Task task, Date date, NotificationSource source);
	public abstract void snoozeAlarm(final Task task, int minutes, NotificationSource source);
	public abstract void setReminder(final Task task);
	public abstract void complete(final Task task);
}
