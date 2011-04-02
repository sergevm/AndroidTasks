package com.softwareprojects.androidtasks.domain.dates;

import java.util.Calendar;

import com.softwareprojects.androidtasks.domain.TaskDateCalculation;

public class MinutesCalculation extends TaskDateCalculation {

	public static final long ONEMINUTEINMILLIS = 1000 * 60;
	
	public MinutesCalculation() {
		super(ONEMINUTEINMILLIS);
	}

	@Override
	protected void applyShiftTo(Calendar calendar, int shift) {
		calendar.add(Calendar.MINUTE, shift);
	}
}
