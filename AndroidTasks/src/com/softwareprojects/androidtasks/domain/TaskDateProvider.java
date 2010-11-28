package com.softwareprojects.androidtasks.domain;

import java.util.Calendar;

public interface TaskDateProvider {
	Calendar getToday();
	Calendar getNow();
}
