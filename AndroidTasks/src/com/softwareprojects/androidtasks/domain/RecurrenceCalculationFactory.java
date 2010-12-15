package com.softwareprojects.androidtasks.domain;

import com.softwareprojects.androidtasks.domain.dates.HoursCalculation;
import com.softwareprojects.androidtasks.domain.dates.MinutesCalculation;
import com.softwareprojects.androidtasks.domain.dates.NullCalculation;
import com.softwareprojects.androidtasks.domain.dates.WeeksCalculation;

public class RecurrenceCalculationFactory implements RecurrenceCalculations {

	@Override
	public TaskDateCalculation create(final Task task) {
	
		switch(task.getRecurrenceType()) {
		case Task.REPEAT_INTERVAL_MINUTES:
			return new MinutesCalculation();
		case Task.REPEAT_INTERVAL_HOURS:
			return new HoursCalculation();
		case Task.REPEAT_INTERVAL_WEEKS:
			return new WeeksCalculation();
		case Task.REPEAT_INTERVAL_MONTHS:
			return new NullCalculation();
		default:
			return new NullCalculation();
		}
	}
}
