package com.softwareprojects.androidtasks.domain.reminders;

import java.util.Calendar;
import java.util.Date;

import com.softwareprojects.androidtasks.domain.Reminder;
import com.softwareprojects.androidtasks.domain.TaskDateProvider;

public class HourlyReminder implements Reminder {

	public static final long ONEHOURINMILLIS = 1000 * 60 * 60;

	@Override
	public Date getNextReminder(Date offset, TaskDateProvider dateProvider) {
		Calendar offsetCalendar = Calendar.getInstance();
		offsetCalendar.setTime(offset);

		Calendar now = dateProvider.getNow();

		long diffInMillis = Math.abs(now.getTimeInMillis() - offsetCalendar.getTimeInMillis());			
		long numberOfFullHours = diffInMillis / ONEHOURINMILLIS;
		
		offsetCalendar.add(Calendar.HOUR, (int)++numberOfFullHours);			
		return offsetCalendar.getTime();	
	}
}
