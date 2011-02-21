package com.softwareprojects.androidtasks.domain;

import java.util.Calendar;
import java.util.List;

public interface TaskRepository {
	
	// CRUD
	Task find(long id);
	void insert(final Task task);
	void update(final Task task);
	
	// Lists
	List<Task> getAll();
	List<Task> getActive(int weeksInThePast, int weeksInTheFuture);
	List<Task> getAll(int weeksInThePast, int weeksInTheFuture);
	List<Task> getDue();
	
	// Custom fetch
	Task findNextOccurrenceOf(final Task task);
	
	// Purging
	void purge(int ageInWeeks);

	// Sync
	List<Task> getNewSince(Calendar lastSyncTime);
	List<Task> getDeletedSince(Calendar lastSyncTime);
	List<Task> getUpdatedSince(Calendar lastSyncTime);
}

