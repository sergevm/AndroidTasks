package com.softwareprojects.androidtasks.db;

import java.util.Calendar;
import java.util.List;

import com.softwareprojects.androidtasks.domain.Task;
import com.softwareprojects.androidtasks.domain.TaskRepository;

public class SqliteTaskRepository implements TaskRepository {

	private final DBHelper dbHelper;
	
	public SqliteTaskRepository(DBHelper dbHelper) {
		this.dbHelper = dbHelper;
	}
	
	@Override
	public void insert(Task task) {
		dbHelper.insert(task);

	}

	@Override
	public void update(Task task) {
		dbHelper.update(task);
	}

	@Override
	public List<Task> getAll() {
		return dbHelper.getAll();
	}

	@Override
	public List<Task> getActive(int weeksInThePast, int weeksInTheFuture) {
		return dbHelper.getActive(weeksInThePast, weeksInTheFuture);
	}

	@Override
	public List<Task> getAll(int weeksInThePast, int weeksInTheFuture) {
		return dbHelper.getAll(weeksInThePast, weeksInTheFuture);
	}

	@Override
	public List<Task> getDue() {
		return dbHelper.getDue();
	}

	@Override
	public Task findNextOccurrenceOf(Task task) {
		if(task.getNextOccurrenceId() == 0) return null;
		return dbHelper.getSingle(task.getNextOccurrenceId());
	}

	@Override
	public void purge(int ageInWeeks) {
		dbHelper.purge(ageInWeeks * 7);
	}

	@Override
	public List<Task> getNewSince(Calendar date) {
		return dbHelper.getNewSince(date);
	}

	@Override
	public List<Task> getDeletedSince(Calendar date) {
		return dbHelper.getDeletedSince(date);
	}

	@Override
	public List<Task> getUpdatedSince(Calendar date) {
		return dbHelper.getUpdatedSince(date);
	}

}
