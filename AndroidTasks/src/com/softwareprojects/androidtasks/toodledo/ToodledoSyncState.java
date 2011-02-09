package com.softwareprojects.androidtasks.toodledo;

import java.util.Calendar;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class ToodledoSyncState {

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
		if (lastSyncTimeInMillis != 0) {
			lastSyncTime = Calendar.getInstance();
			lastSyncTime.setTimeInMillis(lastSyncTimeInMillis);
		}
		else {
			lastSyncTime = Calendar.getInstance();
			lastSyncTime.set(1999, 1, 1);
		}
	}

	public void save() {
		Editor editor = sharedPreferences.edit();
		editor.putLong(LAST_EDIT_TIMESTAMP, getLastEditTimestamp());
		editor.putLong(LAST_DELETE_TIMESTAMP, getLastDeleteTimestamp());
		editor.putLong(LAST_LOCAL_SYNC_TIME, getLastSyncTime().getTimeInMillis());
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

	public SharedPreferences getSharedPreferences() {
		return sharedPreferences;
	}
}
