package com.softwareprojects.androidtasks.domain.sync;

public class SynchronizationSuccess extends SynchronizationResult {

	final static String RESULT_TYPE = "success";
	
	public SynchronizationSuccess() {
		super(RESULT_TYPE);
	}	
}
