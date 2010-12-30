package com.softwareprojects.androidtasks;

public final class Constants {
	public static final String LOGTAG = "AndroidTasks";
	public static final String CURRENT_TASK = "CurrentTask";
	
	public static final String TIME_FORMAT_STRING = "HH:mm";
	public static final String DATE_FORMAT_STRING = "dd-MM-yyyy";
	public static final String DATETIME_FORMAT_STRING = "dd-MM-yyyy HH:mm";
	
	static final String ALARM_TASK_DESCRIPTION = "ALARM_TASK_DESCRIPTION";
	static final String ALARM_TASK_ID = "ALARM_TASK_ID";
	static final String ALARM_SOURCE = "ALARM_SOURCE";
	static final String ALARM_DATE = "ALARM_DATE";
	
	static final int ALARM_NOTIFICATION_ID = 1;
	static final String ANDROIDTASK_TASK_CURRENT_ALARM_URI = "androidtask://task/alarm/";
	static final String ANDROIDTASK_TASK_NEXT_REMINDER_URI = "androidtask://task/reminder/";
	static final String ANDROIDTASK_TASK_NEXT_RECURRENCE_URI = "androidtask://task/recurrent/";
	static final String ANDROIDTASK_TASK_PURGE = "androidtask://tasks/purge/";
	
	static final String PREFS_WEEKS_IN_PAST = "Prefs_list_weeks_in_past";
	static final String PREFS_WEEKS_IN_FUTURE = "Prefs_list_weeks_in_future";
	static final String PREFS_VIBRATE_ON_NOTIFICATION = "Prefs_vibrate_on_notification";
	static final String PREFS_PURGING_TASK_AGE_IN_WEEKS = "Prefs_purge_tasks_with_age";
}
