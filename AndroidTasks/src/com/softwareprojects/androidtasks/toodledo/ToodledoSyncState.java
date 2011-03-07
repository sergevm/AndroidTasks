package com.softwareprojects.androidtasks.toodledo;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.softwareprojects.androidtasks.Constants;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class ToodledoSyncState {
	
	private static final String TAG = ToodledoSyncState.class.getSimpleName();

	private static final String LAST_LOCAL_SYNC_TIME = "LastLocalSyncTime";

	private static final String LAST_DELETE_TIMESTAMP = "LastDeleteTimestamp";

	private static final String LAST_EDIT_TIMESTAMP = "LastEditTimestamp";

	private final SharedPreferences sharedPreferences;

	private Calendar lastSyncTime;
	private long lastEditTimestamp;
	private long lastDeleteTimestamp;

	public ToodledoSyncState(final SharedPreferences sharedPreferences) {
		this.sharedPreferences = sharedPreferences;
		init();
	}

	private void init() {
		
		this.lastEditTimestamp = sharedPreferences.getLong(LAST_EDIT_TIMESTAMP, 0);
		this.lastDeleteTimestamp = sharedPreferences.getLong(LAST_DELETE_TIMESTAMP, 0);
		long lastSyncTimeInMillis = sharedPreferences.getLong(LAST_LOCAL_SYNC_TIME, 0);
		lastSyncTime = getDate(lastSyncTimeInMillis);
		
		Log.d(TAG, String.format("Last Edit Time: %s", getDateAsString(lastEditTimestamp * 1000)));
		Log.d(TAG, String.format("Last Delete Time: %s", getDateAsString(lastDeleteTimestamp * 1000)));
		Log.d(TAG, String.format("Last Local Sync Time: %s", getDateAsString(lastSyncTimeInMillis)));
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
	
	private String getDateAsString(long timeInMillis) {
		Calendar calendar = getDate(timeInMillis);
		SimpleDateFormat format = new SimpleDateFormat(Constants.DATETIME_FORMAT_STRING);
		
		return format.format(calendar.getTime());
	}

	public void save() {
		Editor editor = sharedPreferences.edit();
		
		// Note the substraction of 1, to force picking up items edited at about the same time 
		// as the sync
		editor.putLong(LAST_EDIT_TIMESTAMP, getLastEditTimestamp() - 1);
		editor.putLong(LAST_DELETE_TIMESTAMP, getLastDeleteTimestamp() - 1);
		editor.putLong(LAST_LOCAL_SYNC_TIME, getLastSyncTime().getTimeInMillis() - 1);
		
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
