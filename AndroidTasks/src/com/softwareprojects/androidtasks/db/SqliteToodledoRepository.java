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
	public String findRemoteIdByLocalId(long local_id) {
		return dbHelper.findRemoteIdByLocalId(local_id);
	}

	@Override
	public long findLocalIdByRemoteId(String id) {
		return dbHelper.findLocalIdByRemoteId(id);
	}

	@Override
	public void deleteByLocalId(long id) {
		dbHelper.deleteByLocalId(id);
	}

	@Override
	public void deleteByRemoteId(String id) {
		dbHelper.deleteByRemoteId(id);
	}

}
