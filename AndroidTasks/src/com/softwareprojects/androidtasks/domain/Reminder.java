package com.softwareprojects.androidtasks.domain;

import java.util.Date;

public interface Reminder {
	Date getNextReminder(Date offset, TaskDateProvider dateProvider);
}
