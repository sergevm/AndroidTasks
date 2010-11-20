package com.softwareprojects.androidtasks.domain;

import java.util.Calendar;
import java.util.Date;

public class WeeklyReminder {

	public static final long ONEWEEKINMILLIS = (1000 * 60 * 60 * 24) * 7;

	public static Date getNextReminder(Date date) {

		Calendar targetDate = Calendar.getInstance();
		targetDate.setTime(date);

		Calendar today = Calendar.getInstance();

		long diffInMillis = Math.abs(today.getTimeInMillis() - targetDate.getTimeInMillis());			
		long numberOfFullWeeks = diffInMillis / ONEWEEKINMILLIS;
		
		targetDate.add(Calendar.WEEK_OF_YEAR, (int)++numberOfFullWeeks);			
		return targetDate.getTime();
	}
}
