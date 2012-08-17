package com.softwareprojects.androidtasks.toodledo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.domaindriven.toodledo.ToodledoTimestamp;
import com.softwareprojects.androidtasks.Constants;

public class ToodledoSyncTime {
	
	private static final String TAG = ToodledoSyncTime.class.getSimpleName();

	private static final String LAST_LOCAL_SYNC_TIME = "LastLocalSyncTime";

	private static final String LAST_DELETE_TIMESTAMP = "LastDeleteTimestamp";

	private static final String LAST_EDIT_TIMESTAMP = "LastEditTimestamp";

	private final SharedPreferences sharedPreferences;

	private Calendar lastSyncTime;
	private long lastEditTimestamp;
	private long lastDeleteTimestamp;

	public ToodledoSyncTime(final SharedPreferences sharedPreferences) throws ParseException {
		this.sharedPreferences = sharedPreferences;
		init();
	}

	private void init() throws ParseException {
		
		this.lastEditTimestamp = sharedPreferences.getLong(LAST_EDIT_TIMESTAMP, 0);
		this.lastDeleteTimestamp = sharedPreferences.getLong(LAST_DELETE_TIMESTAMP, 0);
		long lastSyncTimeInMillis = sharedPreferences.getLong(LAST_LOCAL_SYNC_TIME, 0);
		lastSyncTime = getDate(lastSyncTimeInMillis);
		
		Log.d(TAG, String.format("Last Edit Time (GMT): %s", getDateAsString(lastEditTimestamp)));
		Log.d(TAG, String.format("Last Delete Time (GMT): %s", getDateAsString(lastDeleteTimestamp)));
		Log.d(TAG, String.format("Last Local Sync Time: %s", formatDate(lastSyncTime.getTime())));
	}
	
	private Calendar getDate(long timeInMillis) {
		Calendar calendar = Calendar.getInstance();

		if(timeInMillis != 0) {
			calendar.setTimeInMillis(timeInMillis);
			return calendar;
		}

		calendar.set(1999, 1, 1);
		return calendar;
	}
	
	private String getDateAsString(long timeInSeconds) throws ParseException {
		Date localTime = ToodledoTimestamp.GetLocalDateTime(timeInSeconds);
		return formatDate(localTime);
	}
	
	private String formatDate(Date date) {
		SimpleDateFormat format = new SimpleDateFormat(Constants.DATETIME_FORMAT_STRING);
		return format.format(date);
	}

	public void save() {
		Editor editor = sharedPreferences.edit();
		
		editor.putLong(LAST_EDIT_TIMESTAMP, getLastEditTimestamp());
		editor.putLong(LAST_DELETE_TIMESTAMP, getLastDeleteTimestamp());
		editor.putLong(LAST_LOCAL_SYNC_TIME, getLastSyncTime().getTimeInMillis());
		
		editor.commit();
	}

	public void setLastSyncTime(Calendar lastSyncTime) {
		this.lastSyncTime = lastSyncTime;
	}

	public Calendar getLastSyncTime() {
		return lastSyncTime;
	}

	public void setLastEditTimestamp(long lastEditTimestamp) {
		this.lastEditTimestamp = lastEditTimestamp;
	}

	public long getLastEditTimestamp() {
		return lastEditTimestamp;
	}

	public void setLastDeleteTimestamp(long lastDeleteTimestamp) {
		this.lastDeleteTimestamp = lastDeleteTimestamp;
	}

	public long getLastDeleteTimestamp() {
		return lastDeleteTimestamp;
	}
	
	public Calendar getLastEditTime() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(getLastEditTimestamp() * 1000);
		
		return calendar;
	}
	
	public Calendar getLastDeleteTime() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(getLastDeleteTimestamp() * 1000);
		
		return calendar;
	}

	public SharedPreferences getSharedPreferences() {
		return sharedPreferences;
	}
}
