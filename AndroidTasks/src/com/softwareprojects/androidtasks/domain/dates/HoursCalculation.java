package com.softwareprojects.androidtasks.domain.dates;

import java.util.Calendar;

import com.softwareprojects.androidtasks.domain.TaskDateCalculation;

public class HoursCalculation extends TaskDateCalculation {

	public static final long ONEHOURINMILLIS = 1000 * 60 * 60;

	public HoursCalculation() {
		super(ONEHOURINMILLIS);
	}

	@Override
	protected void applyShiftTo(Calendar calendar, int shift) {
		calendar.add(Calendar.HOUR, shift);
	}	
}
