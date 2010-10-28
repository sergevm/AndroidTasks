package com.softwareprojects.androidtasks.domain;

import java.util.Date;

public class Task {		
	public long id;
	public Date targetDate;
	public String description;
	public Boolean completed;
	
	@Override
	public String toString()
	{
		return description;
	}
}
