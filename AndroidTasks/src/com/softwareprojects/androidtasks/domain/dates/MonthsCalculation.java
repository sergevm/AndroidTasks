package com.softwareprojects.androidtasks.domain.dates;

import java.util.Calendar;

import com.softwareprojects.androidtasks.domain.TaskDateCalculation;

public class MonthsCalculation extends TaskDateCalculation {

	public MonthsCalculation() {
		super(0);
	}

	@Override
	protected int calculateMinimumShift(Calendar firstDate, Calendar secondDate) {
		return 
			(secondDate.get(Calendar.YEAR) - firstDate.get(Calendar.YEAR)) * firstDate.getMaximum(Calendar.MONTH) + 
			(secondDate.get(Calendar.MONTH) - firstDate.get(Calendar.MONTH)) - 
			(secondDate.get(Calendar.DAY_OF_MONTH) > firstDate.get(Calendar.DAY_OF_MONTH) ? 0 : 1);
	}

	@Override
	protected void applyShiftTo(Calendar calendar, int shift) {
		calendar.add(Calendar.MONTH, shift);		
	}
}
