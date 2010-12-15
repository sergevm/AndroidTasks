package com.softwareprojects.androidtasks.domain;

import java.util.Date;

public interface TaskDateCalculation {
	Date getNext(Date offset, TaskDateProvider dateProvider, int shift);
}
