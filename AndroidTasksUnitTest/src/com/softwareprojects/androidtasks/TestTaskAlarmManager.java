package com.softwareprojects.androidtasks;

import java.util.Date;

import com.softwareprojects.androidtasks.domain.NotificationSource;
import com.softwareprojects.androidtasks.domain.Task;
import com.softwareprojects.androidtasks.domain.TaskAlarmManager;

public class TestTaskAlarmManager implements TaskAlarmManager {

	@Override
	public void setAlarm(Task task, Date date, NotificationSource source) {
	}

	@Override
	public void snoozeAlarm(Task task, int minutes) {		
	}

}
