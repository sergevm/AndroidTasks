package com.softwareprojects.androidtasks.db;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

import com.softwareprojects.androidtasks.domain.ILog;

import android.util.Log;

public class DbDateFormatter {
	
	private static Pattern dbDateFormatPattern = Pattern.compile(DbConstants.DbDateFormatPattern); 
	private static Pattern dbLegacyDateFormatPattern = Pattern.compile(DbConstants.DbLegacyDateFormatPattern);
	public static SimpleDateFormat dbDateFormat = new SimpleDateFormat(DbConstants.DbDateFormat);
	public static SimpleDateFormat dbLegacyDateFormat = new SimpleDateFormat(DbConstants.DbLegacyDateFormat);
	
	public static ILog log = new ILog() {

		@Override
		public void e(String tag, String message) {
			Log.e(tag, message);
		}

		@Override
		public void v(String tag, String message) {
			Log.v(tag, message);
		}

		@Override
		public void d(String tag, String message) {
			Log.d(tag, message);
		}

		@Override
		public void w(String tag, String message) {
			Log.w(tag, message);
		}

		@Override
		public void i(String tag, String message) {
			Log.i(tag, message);
		}
		
	}; 
	
	private static String TAG = DbDateFormatter.class.getSimpleName();
	
	public static Date parse(String dateAsString) throws ParseException {
		
		if(dbDateFormatPattern.matcher(dateAsString).matches()) {
			
			return dbDateFormat.parse(dateAsString);
		}
		else if(dbLegacyDateFormatPattern.matcher(dateAsString).find()){
			
			log.w(TAG, String.format("%s is a legacy date representation in the Android database", dateAsString));
			return dbLegacyDateFormat.parse(dateAsString);
		}
		else {
			
			log.e(TAG, String.format("%s is not a valid date representation in the AndroidTasks database", dateAsString));
			return null;
		}
	}
	
	public static String format(Calendar calendar) {
		
		return format(calendar.getTime());
	}
	
	public static String format(Date date) {
		
		return dbDateFormat.format(date);
	}
}
