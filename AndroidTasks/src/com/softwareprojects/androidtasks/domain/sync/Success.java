package com.softwareprojects.androidtasks.domain.sync;

public class Success extends SynchronizationResult {

	final static String RESULT_TYPE = "success";
	
	public Success() {
		super(RESULT_TYPE);
	}	
}
