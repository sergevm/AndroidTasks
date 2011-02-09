package com.softwareprojects.androidtasks.domain.sync;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.softwareprojects.androidtasks.Constants;

public abstract class SynchronizationResult {
	
	private final String tag;
	private final Date synchronizationTime;
	
	protected SynchronizationResult(final String tag) {
		this.synchronizationTime = new Date();
		this.tag = tag;
	}
		
	@Override
	public String toString() {
		return String.format("Synchronization was %s on %s", tag,
				new SimpleDateFormat(Constants.DATETIME_FORMAT_STRING).format(synchronizationTime));
	}
}