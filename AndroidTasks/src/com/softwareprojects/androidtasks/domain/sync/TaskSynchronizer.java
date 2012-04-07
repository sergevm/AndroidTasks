package com.softwareprojects.androidtasks.domain.sync;

import java.text.ParseException;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.json.JSONException;

import com.softwareprojects.androidtasks.domain.Task;

public interface TaskSynchronizer {

	void init(final String user, final String password, String appId, String appToken);
	
	SynchronizationResult addTasks(final List<Task> tasks) throws JSONException, Exception;
	SynchronizationResult deleteTasks(final List<Task> tasks) throws JSONException, Exception;
	SynchronizationResult updateTasks(final List<Task> tasks);

	Map<String,Task> getUpdated() throws JSONException, Exception;
	List<Long> getDeleted() throws JSONException, Exception;
		
	Calendar getLastEditTime();
	Calendar getLastDeleteTime();

	void register(Map<String,Task> tasks) throws ParseException;
	void unregister(List<Long> tasks);
	
	void updateSyncStatus(Calendar localSyncTime);
}
