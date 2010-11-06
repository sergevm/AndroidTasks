package com.softwareprojects.androidtasks.domain;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.softwareprojects.androidtasks.Constants;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class Task implements Parcelable{		
	public long id;
	public Date targetDate;
	public String description;
	public boolean completed;

	private static final String CLASSNAME = Task.class.getSimpleName();

	@Override
	public String toString()
	{
		return description;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// id
		dest.writeLong(id);
		
		// target date
		if (targetDate != null) {
			dest.writeString(new SimpleDateFormat(Constants.DATETIME_FORMAT_STRING)
			.format(targetDate));
		}
		else {
			dest.writeString(null);
		}
		
		// destination
		dest.writeString(description);
		
		// completed
		dest.writeBooleanArray(new boolean[]{completed});
	}

	public static final Parcelable.Creator<Task> CREATOR = 
		new Parcelable.Creator<Task>() {
		public Task createFromParcel(Parcel in) {
			return new Task(in);
		}

		@Override
		public Task[] newArray(int size) {
			return new Task[size];
		}
	};

	private Task(Parcel parcel) {
		//id
		id = parcel.readLong();
		
		// date
		try {
			String dateAsString = parcel.readString();
			if(dateAsString != null) {				
				targetDate = new SimpleDateFormat(Constants.DATETIME_FORMAT_STRING).parse(dateAsString);
			}
		} catch (ParseException e) {
			Log.e(Constants.LOGTAG, CLASSNAME, e);
		}

		// description
		description = parcel.readString();

		// Boolean values grouped in a single array
		boolean[] buffer = parcel.createBooleanArray();
		completed = buffer[0];
}

	public Task() {
	}
}
