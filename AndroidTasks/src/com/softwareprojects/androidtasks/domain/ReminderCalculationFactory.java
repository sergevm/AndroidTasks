package com.softwareprojects.androidtasks.domain;

import com.softwareprojects.androidtasks.domain.dates.DaysCalculation;
import com.softwareprojects.androidtasks.domain.dates.MinutesCalculation;
import com.softwareprojects.androidtasks.domain.dates.HoursCalculation;
import com.softwareprojects.androidtasks.domain.dates.NullCalculation;
import com.softwareprojects.androidtasks.domain.dates.WeeksCalculation;

public class ReminderCalculationFactory implements ReminderCalculations {

	@Override
	public TaskDateCalculation create(Task task) {
		switch (task.getReminderType()) {
		case Task.REMINDER_EVERYMINUTE:
			return new MinutesCalculation();
		case Task.REMINDER_WEEKLY:
			return new WeeksCalculation();
		case Task.REMINDER_HOURLY:
			return new HoursCalculation();
		case Task.REMINDER_DAILY:
			return new DaysCalculation();
		default:
			return new NullCalculation();
		}
	}

}
