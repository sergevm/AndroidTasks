package com.softwareprojects.androidtasks.domain.sync;

public class NoSync extends SynchronizationResult {

	final static String RESULT_TYPE = "no sync";

	public NoSync() {
		super(RESULT_TYPE);
	}
	
	@Override
	public String toString() {
		return "There were no items so sync";
	}
}
