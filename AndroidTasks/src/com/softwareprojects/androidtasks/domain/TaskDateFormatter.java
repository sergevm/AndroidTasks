package com.softwareprojects.androidtasks.domain;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.softwareprojects.androidtasks.Constants;

public class TaskDateFormatter {
	private static final SimpleDateFormat dateFormatWithTime = new SimpleDateFormat(Constants.DATETIME_FORMAT_STRING);
	private static final SimpleDateFormat dateFormatWithoutTime = new SimpleDateFormat(Constants.DATE_FORMAT_STRING);
	
	public static Date getToday() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		
		return calendar.getTime();
	}
	
	public static String format(Date date) {
		if(hasTime(date)) {
			return formatWithTime(date);
		}
		
		return formatWithoutTime(date);
	}
	
	public static boolean hasTime(Date date) {
		return date.getHours() != 0 | date.getMinutes() != 0;
	}
	
	public static String formatWithTime(Date date) {
		return dateFormatWithTime.format(date);
	}

	public static String formatWithoutTime(Date date) {
		return dateFormatWithoutTime.format(date);
	}
}
