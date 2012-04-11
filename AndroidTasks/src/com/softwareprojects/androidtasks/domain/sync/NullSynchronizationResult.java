package com.softwareprojects.androidtasks.domain.sync;

public class NullSynchronizationResult extends SynchronizationResult {

	final static String RESULT_TYPE = "no sync";

	public NullSynchronizationResult(final String message) {
		super(RESULT_TYPE, message);
	}
}
