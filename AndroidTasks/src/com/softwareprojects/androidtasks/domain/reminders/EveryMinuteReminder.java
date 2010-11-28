package com.softwareprojects.androidtasks.domain.reminders;

import java.util.Calendar;
import java.util.Date;

import com.softwareprojects.androidtasks.domain.Reminder;
import com.softwareprojects.androidtasks.domain.TaskDateProvider;

public class EveryMinuteReminder implements Reminder {

	public static final long ONEMINUTEINMILLIS = 1000 * 60;

	public Date getNextReminder(Date offset, TaskDateProvider dateProvider) {

		Calendar offsetCalendar = Calendar.getInstance();
		offsetCalendar.setTime(offset);

		Calendar now = dateProvider.getNow();

		long diffInMillis = Math.abs(now.getTimeInMillis() - offsetCalendar.getTimeInMillis());			
		long numberOfFullMinutes = diffInMillis / ONEMINUTEINMILLIS;
		
		offsetCalendar.add(Calendar.MINUTE, (int)++numberOfFullMinutes);			
		return offsetCalendar.getTime();
	}
}
