package com.softwareprojects.androidtasks.domain.dates;

import java.util.Calendar;
import java.util.Date;

import com.softwareprojects.androidtasks.domain.TaskDateCalculation;
import com.softwareprojects.androidtasks.domain.TaskDateProvider;

public class NullCalculation extends TaskDateCalculation {

	public NullCalculation() {
		super(0L);
	}

	@Override
	public Date getNext(Date offset, TaskDateProvider dateProvider, int shift) {
		return null;
	}

	@Override
	protected void applyShiftTo(Calendar calendar, int shift) {
	}
}
