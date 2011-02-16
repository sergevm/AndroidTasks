package com.softwareprojects.androidtasks.toodledo;

public interface ToodledoRepository {
	public String find(long id);
	public void insert(long local_id, String remote_id, long timestamp);
}
