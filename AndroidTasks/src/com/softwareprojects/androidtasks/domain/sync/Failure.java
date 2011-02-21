package com.softwareprojects.androidtasks.domain.sync;

public class Failure extends SynchronizationResult {

	final static String SYNCRESULT = "Failure";
	private final String failureMessage;
	
	public Failure(final String failureMessage) {
		super(SYNCRESULT);
		this.failureMessage = failureMessage;
	}
	
	@Override
	public String toString() {
		return failureMessage;
	}
}

