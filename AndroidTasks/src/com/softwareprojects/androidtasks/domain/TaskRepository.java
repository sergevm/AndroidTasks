package com.softwareprojects.androidtasks.domain;

import java.util.List;

public interface TaskRepository {
	
	// CRUD
	void insert(final Task task);
	void update(final Task task);
	
	// Lists
	List<Task> getAll();
	List<Task> getActive(int weeksInThePast, int weeksInTheFuture);
	List<Task> getAll(int weeksInThePast, int weeksInTheFuture);
	List<Task> getDue();
	
	// Custom fetch
	Task findNextOccurrenceOf(final Task task);
}
