package com.softwareprojects.androidtasks.domain.dates;


import java.util.Calendar;

import com.softwareprojects.androidtasks.domain.TaskDateCalculation;

public class DaysCalculation extends TaskDateCalculation {

	public static final long ONEDAYINMILLIS = 1000 * 60 * 60 * 24;

	public DaysCalculation() {
		super(ONEDAYINMILLIS);
	}

	@Override
	protected void applyShiftTo(Calendar calendar, int shift) {
		calendar.add(Calendar.DATE, shift);
	}
}
