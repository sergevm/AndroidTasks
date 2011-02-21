package com.softwareprojects.androidtasks.toodledo;

public interface ToodledoRepository {
	
	public String findRemoteIdByLocalId(long id);
	public long findLocalIdByRemoteId(String id);

	public void deleteByLocalId(long id);
	public void deleteByRemoteId(String id);
	
	public void insert(long local_id, String remote_id, long timestamp);
}
