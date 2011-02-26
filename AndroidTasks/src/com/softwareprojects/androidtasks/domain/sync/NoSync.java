package com.softwareprojects.androidtasks.domain.sync;

public class NoSync extends SynchronizationResult {

	final static String RESULT_TYPE = "no sync";

	public NoSync(final String message) {
		super(RESULT_TYPE, message);
	}
}
