package com.softwareprojects.androidtasks.domain.sync;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.softwareprojects.androidtasks.Constants;

public abstract class SynchronizationResult {
	
	private final String tag;
	private final String message;
	private final Date synchronizationTime;
	
	protected SynchronizationResult(final String tag) {
		this.tag = tag;
		this.message = null;
		this.synchronizationTime = new Date();
	}
	
	protected SynchronizationResult(final String tag, final String message) {
		this.tag = tag;
		this.message = message;
		this.synchronizationTime = new Date();
	}
		
	@Override
	public String toString() {
		
		if(message == null) {
			return formatDefaultMessage();
		}
		
		return message;
	}
	
	private String formatDefaultMessage() {
		return String.format("Synchronization was %s on %s", tag,
				new SimpleDateFormat(Constants.DATETIME_FORMAT_STRING).format(synchronizationTime));		
	}
}