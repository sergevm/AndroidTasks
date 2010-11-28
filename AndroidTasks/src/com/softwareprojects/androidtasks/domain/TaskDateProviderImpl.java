package com.softwareprojects.androidtasks.domain;

import java.util.Calendar;

public class TaskDateProviderImpl implements TaskDateProvider {

	@Override
	public Calendar getToday() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(TaskDateFormatter.getToday());
		
		return calendar;
	}

	@Override
	public Calendar getNow() {
		return Calendar.getInstance();
	}

}
