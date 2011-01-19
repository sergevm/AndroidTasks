package com.softwareprojects.androidtasks.helpers;

public class ToodledoAccountInfo {
	private String userId;
	private long lastEditTask;
	private long lastDeleteTask;
	private String dateFormat;
	
	public ToodledoAccountInfo(final String userId) {
		this.userId = userId;
	}
	
	public void setLastEditTask(long lastEditTask) {
		this.lastEditTask = lastEditTask;
	}
	
	public long getLastEditTask() {
		return lastEditTask;
	}
	
	public void setLastDeleteTask(long lastDeleteTask) {
		this.lastDeleteTask = lastDeleteTask;
	}
	
	public long getLastDeleteTask() {
		return lastDeleteTask;
	}

	public String getUserId() {
		return userId;
	}

	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}

	public String getDateFormat() {
		return dateFormat;
	}
}
