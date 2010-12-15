package com.softwareprojects.androidtasks.domain.dates;

import java.util.Calendar;
import java.util.Date;

import com.softwareprojects.androidtasks.domain.TaskDateCalculation;
import com.softwareprojects.androidtasks.domain.TaskDateProvider;

public class WeeksCalculation implements TaskDateCalculation {

	public static final long ONEWEEKINMILLIS = (1000 * 60 * 60 * 24) * 7;

	public Date getNext(Date offset, TaskDateProvider dateProvider, int shift) {

		Calendar offsetCalendar = Calendar.getInstance();
		offsetCalendar.setTime(offset);

		Calendar today = dateProvider.getToday();

		long diffInMillis = Math.abs(today.getTimeInMillis() - offsetCalendar.getTimeInMillis());			
		long numberOfFullWeeks = diffInMillis / ONEWEEKINMILLIS;
		
		offsetCalendar.add(Calendar.WEEK_OF_YEAR, (int)numberOfFullWeeks + shift);			
		return offsetCalendar.getTime();
	}
}
