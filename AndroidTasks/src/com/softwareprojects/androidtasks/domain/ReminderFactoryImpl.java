package com.softwareprojects.androidtasks.domain;

import com.softwareprojects.androidtasks.domain.reminders.DailyReminder;
import com.softwareprojects.androidtasks.domain.reminders.EveryMinuteReminder;
import com.softwareprojects.androidtasks.domain.reminders.HourlyReminder;
import com.softwareprojects.androidtasks.domain.reminders.WeeklyReminder;

public class ReminderFactoryImpl implements ReminderFactory {

	@Override
	public Reminder create(Task task) {
		switch (task.getReminder()) {
		case Task.REMINDER_EVERYMINUTE:
			return new EveryMinuteReminder();
		case Task.REMINDER_WEEKLY:
			return new WeeklyReminder();
		case Task.REMINDER_HOURLY:
			return new HourlyReminder();
		case Task.REMINDER_DAILY:
			return new DailyReminder();
		default:
			return new WeeklyReminder();
		}
	}

}
