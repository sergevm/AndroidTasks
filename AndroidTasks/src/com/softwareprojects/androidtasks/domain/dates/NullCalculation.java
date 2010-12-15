package com.softwareprojects.androidtasks.domain.dates;

import java.util.Date;

import com.softwareprojects.androidtasks.domain.TaskDateCalculation;
import com.softwareprojects.androidtasks.domain.TaskDateProvider;

public class NullCalculation implements TaskDateCalculation {

	@Override
	public Date getNext(Date offset, TaskDateProvider dateProvider, int shift) {
		return null;
	}

}
