package com.softwareprojects.androidtasks.domain.dates;

import java.util.Calendar;
import java.util.Date;

import com.softwareprojects.androidtasks.domain.TaskDateCalculation;
import com.softwareprojects.androidtasks.domain.TaskDateProvider;

public class HoursCalculation implements TaskDateCalculation {

	public static final long ONEHOURINMILLIS = 1000 * 60 * 60;

	@Override
	public Date getNext(Date offset, TaskDateProvider dateProvider, int shift) {
		Calendar offsetCalendar = Calendar.getInstance();
		offsetCalendar.setTime(offset);

		Calendar now = dateProvider.getNow();

		long diffInMillis = Math.abs(now.getTimeInMillis() - offsetCalendar.getTimeInMillis());			
		long numberOfFullHours = diffInMillis / ONEHOURINMILLIS;
		
		offsetCalendar.add(Calendar.HOUR, (int)numberOfFullHours + shift);			
		return offsetCalendar.getTime();	
	}
}
