package com.softwareprojects.androidtasks.domain.sync;

import java.util.Calendar;
import java.util.List;

import org.json.JSONException;

import com.softwareprojects.androidtasks.domain.Task;

public interface Synchronizer {
	
	SynchronizationResult addTasks(final List<Task> tasks) throws JSONException, Exception;
	SynchronizationResult deleteTasks(final List<Task> tasks) throws JSONException, Exception;
	SynchronizationResult updateTasks(final List<Task> tasks);

	List<Task> getUpdated() throws JSONException, Exception;
	List<Long> getDeleted() throws JSONException, Exception;
		
	Calendar getLastEditTime();
	Calendar getLastDeleteTime();
	
	void updateSyncStatus(Calendar localSyncTime);
}
