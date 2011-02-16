package com.softwareprojects.androidtasks.db;

import com.softwareprojects.androidtasks.toodledo.ToodledoRepository;

public class SqliteToodledoRepository implements ToodledoRepository{

	private ToodledoDBHelper dbHelper;

	public SqliteToodledoRepository(ToodledoDBHelper dbHelper) {
		this.dbHelper = dbHelper;
	}
	
	@Override
	public void insert(long local_id, String remote_id, long timestamp) {
		dbHelper.insert(local_id, remote_id, timestamp);
	}

	@Override
	public String find(long local_id) {
		return dbHelper.find(local_id);
	}

}
