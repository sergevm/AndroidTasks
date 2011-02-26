package com.softwareprojects.androidtasks.domain.sync;

public class Failure extends SynchronizationResult {

	final static String SYNCRESULT = "Failure";
	
	public Failure() {
		super(SYNCRESULT);
	}
	
	public Failure(final String failureMessage) {
		super(SYNCRESULT, failureMessage);
	}
}

