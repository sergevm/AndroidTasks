package com.softwareprojects.androidtasks.domain.dates;

import java.util.Calendar;
import java.util.Date;

import com.softwareprojects.androidtasks.domain.TaskDateCalculation;
import com.softwareprojects.androidtasks.domain.TaskDateProvider;

public class MinutesCalculation implements TaskDateCalculation {

	public static final long ONEMINUTEINMILLIS = 1000 * 60;

	public Date getNext(Date offset, TaskDateProvider dateProvider, int shift) {

		Calendar offsetCalendar = Calendar.getInstance();
		offsetCalendar.setTime(offset);

		Calendar now = dateProvider.getNow();

		long diffInMillis = Math.abs(now.getTimeInMillis() - offsetCalendar.getTimeInMillis());			
		long numberOfFullMinutes = diffInMillis / ONEMINUTEINMILLIS;
		
		offsetCalendar.add(Calendar.MINUTE, (int)numberOfFullMinutes + shift);			
		return offsetCalendar.getTime();
	}
}
