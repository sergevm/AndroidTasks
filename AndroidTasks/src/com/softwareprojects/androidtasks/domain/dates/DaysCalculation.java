package com.softwareprojects.androidtasks.domain.dates;

import java.util.Calendar;
import java.util.Date;

import com.softwareprojects.androidtasks.domain.TaskDateCalculation;
import com.softwareprojects.androidtasks.domain.TaskDateProvider;

public class DaysCalculation implements TaskDateCalculation {

	public static final long ONEDAYINMILLIS = 1000 * 60 * 60 * 24;

	@Override
	public Date getNext(Date offset, TaskDateProvider dateProvider, int shift) {
		
		if(offset == null) return null;
		
		Calendar offsetCalendar = Calendar.getInstance();
		offsetCalendar.setTime(offset);

		Calendar now = dateProvider.getNow();

		if(offsetCalendar.after(now)) return offset;
		
		long diffInMillis = Math.abs(now.getTimeInMillis() - offsetCalendar.getTimeInMillis());			
		long numberOfFullDays = diffInMillis / ONEDAYINMILLIS;
		
		offsetCalendar.add(Calendar.DATE, (int)numberOfFullDays + shift);			
		return offsetCalendar.getTime();	
	}
}
