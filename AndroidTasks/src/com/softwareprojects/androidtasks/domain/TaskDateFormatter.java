package com.softwareprojects.androidtasks.domain;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.softwareprojects.androidtasks.Constants;

public class TaskDateFormatter {
	private static final SimpleDateFormat dateFormatWithTime = new SimpleDateFormat(Constants.DATETIME_FORMAT_STRING);
	private static final SimpleDateFormat dateFormatWithoutTime = new SimpleDateFormat(Constants.DATE_FORMAT_STRING);
	
	public static String Format(Date date) {
		if(date.getHours() == 0 & date.getMinutes() == 0) {
			return dateFormatWithoutTime.format(date);
		}
		
		return dateFormatWithTime.format(date);
	}
}
