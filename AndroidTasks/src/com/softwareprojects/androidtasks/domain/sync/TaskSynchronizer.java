package com.softwareprojects.androidtasks.domain.sync;

import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.json.JSONException;

import com.domaindriven.toodledo.SyncException;
import com.softwareprojects.androidtasks.domain.Task;

public interface TaskSynchronizer {

	void init(final String user, final String password, String appId, String appToken) throws IOException, SyncException;
	
	SynchronizationResult addTasks(final List<Task> tasks);
	SynchronizationResult deleteTasks(final List<Task> tasks);
	SynchronizationResult updateTasks(final List<Task> tasks);

	Map<String,Task> getUpdated() throws SyncException, IOException;
	List<Long> getDeleted() throws SyncException, IOException;
		
	Calendar getLastEditTime();
	Calendar getLastDeleteTime();

	void register(Map<String,Task> tasks) throws ParseException;
	void unregister(List<Long> tasks);
	
	void updateSyncStatus(Calendar localSyncTime) throws IOException, SyncException;
}
