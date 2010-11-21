package com.softwareprojects.androidtasks.domain;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.softwareprojects.androidtasks.Constants;

public class Task implements Parcelable, Cloneable{		
	private long id;
	private Date targetDate;
	private String description;
	private boolean completed;
	private int snoozeCount = 0;
	private String notes;
	private String location;
	private int reminder;
	private Date reminderDate;	

	private static final String CLASSNAME = Task.class.getSimpleName();

	public static final int REMINDER_MANUAL = 0;
	public final static int REMINDER_HOURLY = 1;
	public final static int REMINDER_DAILY = 2;
	public final static int REMINDER_WEEKLY = 3;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isCompleted() {
		return completed;
	}

	public void setCompleted(boolean completed) {
		this.completed = completed;
	}

	public int getSnoozeCount() {
		return snoozeCount;
	}

	public void setSnoozeCount(int snoozeCount) {
		this.snoozeCount = snoozeCount;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public int getReminder() {
		return reminder;
	}

	public void setReminder(int reminder) {
		this.reminder = reminder;
	}

	public Date getReminderDate() {
		return reminderDate;
	}

	public void setReminderDate(Date reminderDate) {
		this.reminderDate = reminderDate;
	}

	public Date getTargetDate() {
		return targetDate;
	}

	public void setTargetDate(Date targetDate) {
		this.targetDate = targetDate;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

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

		// snoozeCount
		dest.writeInt(snoozeCount);

		// notes
		dest.writeString(notes);

		// location
		dest.writeString(location);

		// reminder
		dest.writeInt(reminder);

		// reminderDate
		if(reminderDate != null) {
			dest.writeString(new SimpleDateFormat(Constants.DATETIME_FORMAT_STRING)
			.format(reminderDate));
		}
	}

	public static final Parcelable.Creator<Task> CREATOR = 
		new Parcelable.Creator<Task>() {
		@Override
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

		// snoozeCount
		snoozeCount = parcel.readInt();

		// notes
		notes = parcel.readString();

		// location
		location = parcel.readString();

		// reminder
		reminder = parcel.readInt();

		// reminderDate
		try {
			String dateAsString = parcel.readString();
			if(dateAsString != null) {				
				reminderDate = new SimpleDateFormat(Constants.DATETIME_FORMAT_STRING).parse(dateAsString);
			}
		} catch (ParseException e) {
			Log.e(Constants.LOGTAG, CLASSNAME, e);
		}

	}

	public Task() {
	}

	public boolean canHaveReminder() {
		return completed == false;
	}

	public void set(final TaskAlarmManager alarmManager) {		

		NotificationSource source = NotificationSource.ALARMSOURCE_NONE;
		Date now = new Date();

		if(reminder == REMINDER_MANUAL & targetDate != null) {
			reminderDate = targetDate;
		}

		if(targetDate == null) {
			if(reminderDate == null) {
				if(reminder != REMINDER_MANUAL) {
					reminderDate = WeeklyReminder.getNextReminder(TaskDateFormatter.getToday());	
					source = NotificationSource.ALARMSOURCE_REMINDERDATE;
				}
			}
			else if(reminderDate != null) {
				if(reminder != REMINDER_MANUAL) {
					if(reminderDate.before(now)) {
						reminderDate = WeeklyReminder.getNextReminder(reminderDate);
						source = NotificationSource.ALARMSOURCE_REMINDERDATE;
					}
				}
				else {
					reminderDate = null;
				}
			}
		}
		else if(targetDate != null) {
			if(reminderDate.equals(targetDate) == false) {
				if(reminderDate.before(now)) {
					reminderDate = WeeklyReminder.getNextReminder(targetDate);
					source = NotificationSource.ALARMSOURCE_TARGETDATE;
				}
			}
			else if(reminderDate.equals(targetDate)) {
				if(reminderDate.before(now)) {
					reminderDate = WeeklyReminder.getNextReminder(targetDate);
					source = NotificationSource.ALARMSOURCE_TARGETDATE;					
				}
			}
		}

		alarmManager.setAlarm(this, reminderDate, source);
	}

	public void snooze(final TaskAlarmManager alarmManager, int minutes, NotificationSource notificationType) {

		switch(notificationType) {
		case ALARMSOURCE_REMINDERDATE:
		case ALARMSOURCE_SNOOZE_REMINDERDATE:

			Calendar snoozeTargetTime = Calendar.getInstance();
			snoozeTargetTime.add(Calendar.MINUTE, minutes);

			alarmManager.snoozeAlarm(this, minutes, NotificationSource.ALARMSOURCE_SNOOZE_REMINDERDATE);

			break;
		case ALARMSOURCE_TARGETDATE:
		case ALARMSOURCE_SNOOZE_TARGETDATE:

			assert targetDate != null;
			assert reminderDate != null;

			alarmManager.snoozeAlarm(this, minutes, NotificationSource.ALARMSOURCE_SNOOZE_TARGETDATE);
			break;
		}
	}
}
