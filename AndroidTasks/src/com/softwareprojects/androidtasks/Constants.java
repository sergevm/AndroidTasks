package com.softwareprojects.androidtasks;

public final class Constants {
	public static final String LOGTAG = "AndroidTasks";
	public static final String CURRENT_TASK = "CurrentTask";
	
	public static final String TIME_FORMAT_STRING = "HH:mm";
	public static final String DATE_FORMAT_STRING = "dd-MM-yyyy";
	public static final String DATETIME_FORMAT_STRING = "dd-MM-yyyy HH:mm";
	
	public static final String ALARM_TASK_DESCRIPTION = "ALARM_TASK_DESCRIPTION";
	public static final String ALARM_TASK_ID = "ALARM_TASK_ID";
	public static final String ALARM_SOURCE = "ALARM_SOURCE";
	public static final String ALARM_DATE = "ALARM_DATE";
	
	public static final int ALARM_NOTIFICATION_ID = 1;
	public static final String ANDROIDTASK_TASK_CURRENT_ALARM_URI = "androidtask://task/alarm/";
	public static final String ANDROIDTASK_TASK_NEXT_REMINDER_URI = "androidtask://task/reminder/";
	public static final String ANDROIDTASK_TASK_NEXT_RECURRENCE_URI = "androidtask://task/recurrent/";
	public static final String ANDROIDTASK_TASK_PURGE = "androidtask://tasks/purge/";
	public static final String ANDROIDTASK_TASK_SYNC = "androidtask://tasks/sync/";
	
	public static final String PREFS_WEEKS_IN_PAST = "Prefs_list_weeks_in_past";
	public static final String PREFS_WEEKS_IN_FUTURE = "Prefs_list_weeks_in_future";
	public static final String PREFS_VIBRATE_ON_NOTIFICATION = "Prefs_vibrate_on_notification";
	public static final String PREFS_PURGING_TASK_AGE_IN_WEEKS = "Prefs_purge_tasks_with_age";
	public static final String PREFS_TOODLEDO_PWD = "Prefs_toodledo_sync_pwd";
	public static final String PREFS_SYNC_WITH_TOODLEDO = "Prefs_sync_with_toodledo";
	public static final String PREFS_TOODLEDO_USER = "Prefs_toodledo_sync_user";
}
