package com.softwareprojects.androidtasks.domain;

public enum NotificationSource {
	ALARMSOURCE_NONE,
	ALARMSOURCE_TARGETDATE,
	ALARMSOURCE_REMINDERDATE,
	ALARMSOURCE_SNOOZE_TARGETDATE,
	ALARMSOURCE_SNOOZE_REMINDERDATE,
	ALARMSOURCE_RECURRENCY, 
	ALARMSOURCE_PURGE;

	public boolean isReminder() {
		
		return this == NotificationSource.ALARMSOURCE_REMINDERDATE || 
		this == NotificationSource.ALARMSOURCE_SNOOZE_REMINDERDATE;
	}
}
