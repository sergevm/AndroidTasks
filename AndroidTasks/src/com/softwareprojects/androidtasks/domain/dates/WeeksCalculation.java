package com.softwareprojects.androidtasks.domain.dates;

import java.util.Calendar;

import com.softwareprojects.androidtasks.domain.TaskDateCalculation;

public class WeeksCalculation extends TaskDateCalculation {

	public static final long ONEWEEKINMILLIS = (1000 * 60 * 60 * 24) * 7;
	
	public WeeksCalculation() {
		super(ONEWEEKINMILLIS);
	}

	@Override
	protected void applyShiftTo(Calendar calendar, int shift) {
		calendar.add(Calendar.WEEK_OF_YEAR, shift);
	}
}
