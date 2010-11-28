package com.softwareprojects.androidtasks.domain.reminders;

import java.util.Calendar;
import java.util.Date;

import com.softwareprojects.androidtasks.domain.Reminder;
import com.softwareprojects.androidtasks.domain.TaskDateProvider;

public class WeeklyReminder implements Reminder {

	public static final long ONEWEEKINMILLIS = (1000 * 60 * 60 * 24) * 7;

	public Date getNextReminder(Date offset, TaskDateProvider dateProvider) {

		Calendar offsetCalendar = Calendar.getInstance();
		offsetCalendar.setTime(offset);

		Calendar today = dateProvider.getToday();

		long diffInMillis = Math.abs(today.getTimeInMillis() - offsetCalendar.getTimeInMillis());			
		long numberOfFullWeeks = diffInMillis / ONEWEEKINMILLIS;
		
		offsetCalendar.add(Calendar.WEEK_OF_YEAR, (int)++numberOfFullWeeks);			
		return offsetCalendar.getTime();
	}
}
