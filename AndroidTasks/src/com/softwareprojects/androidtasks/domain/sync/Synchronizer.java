package com.softwareprojects.androidtasks.domain.sync;

import java.util.Calendar;
import java.util.List;

import com.softwareprojects.androidtasks.domain.Task;

public interface Synchronizer {
	
	SynchronizationResult addTasks(final List<Task> tasks);
	SynchronizationResult deleteTasks(final List<Task> tasks);
	SynchronizationResult updateTasks(final List<Task> tasks);

	List<Task> getNew();
	List<Task> getDeleted();
	List<Task> getUpdated();
		
	Calendar getLastSyncTime();
}
