package com.softwareprojects.androidtasks.domain;

import java.util.Date;

public interface TaskAlarmManager {
	public abstract void setAlarm(final Task task, Date date, NotificationSource source);
	public abstract void snoozeAlarm(Task task, int minutes, NotificationSource source);
}
