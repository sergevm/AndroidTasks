package com.softwareprojects.androidtasks.domain;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.softwareprojects.androidtasks.Constants;

public class Task implements Parcelable, Cloneable {
	private long id;
	private Date targetDate;
	private String description;
	private boolean completed;
	private int snoozeCount = 0;
	private String notes;
	private String location;
	private int reminderType;
	private Date reminderDate;
	private int recurrenceType;
	private int recurrenceValue;
	private long nextOccurrenceId;
	

	private static final String CLASSNAME = Task.class.getSimpleName();

	public static final int REMINDER_MANUAL = 0;
	public final static int REMINDER_EVERYMINUTE = 1;
	public final static int REMINDER_HOURLY = 2;
	public final static int REMINDER_DAILY = 3;
	public final static int REMINDER_WEEKLY = 4;

	public static final int REPEAT_NONE = 0;
	public static final int REPEAT_INTERVAL_MINUTES = 1;
	public static final int REPEAT_INTERVAL_HOURS = 2;
	public static final int REPEAT_INTERVAL_DAYS = 3;
	public static final int REPEAT_INTERVAL_WEEKS = 4;
	public static final int REPEAT_INTERVAL_MONTHS = 5;

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

	public int getReminderType() {
		return reminderType;
	}

	public void setReminderType(int reminder) {
		this.reminderType = reminder;
	}

	public Date getReminderDate() {
		return reminderDate;
	}

	public void setReminderDate(Date reminderDate) {
		this.reminderDate = reminderDate;
	}

	public void setRecurrenceType(int recurrenceType) {
		this.recurrenceType = recurrenceType;
	}

	public int getRecurrenceType() {
		return recurrenceType;
	}

	public void setRecurrenceValue(int recurrenceValue) {
		this.recurrenceValue = recurrenceValue;
	}

	public int getRecurrenceValue() {
		return recurrenceValue;
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

	public void setNextOccurrenceId(long nextOccurrenceId) {
		this.nextOccurrenceId = nextOccurrenceId;
	}

	public long getNextOccurrenceId() {
		return nextOccurrenceId;
	}

	@Override
	public String toString() {
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
			dest.writeString(new SimpleDateFormat(
					Constants.DATETIME_FORMAT_STRING).format(targetDate));
		} else {
			dest.writeString(null);
		}

		// destination
		dest.writeString(description);

		// completed
		dest.writeBooleanArray(new boolean[] { completed });

		// snoozeCount
		dest.writeInt(snoozeCount);

		// notes
		dest.writeString(notes);

		// location
		dest.writeString(location);

		// reminder
		dest.writeInt(reminderType);

		// reminderDate
		if (reminderDate != null) {
			dest.writeString(new SimpleDateFormat(
					Constants.DATETIME_FORMAT_STRING).format(reminderDate));
		}
		else {
			dest.writeString(null);
		}

		// recurrencyType
		dest.writeInt(recurrenceType);

		// recurrencyValue
		dest.writeInt(recurrenceValue);
		
		// nextOccurrencId
		dest.writeLong(nextOccurrenceId);
	}

	public static final Parcelable.Creator<Task> CREATOR = new Parcelable.Creator<Task>() {
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
		// id
		id = parcel.readLong();

		// date
		try {
			String dateAsString = parcel.readString();
			if (dateAsString != null) {
				targetDate = new SimpleDateFormat(
						Constants.DATETIME_FORMAT_STRING).parse(dateAsString);
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
		reminderType = parcel.readInt();

		// reminderDate
		try {
			String dateAsString = parcel.readString();
			if (dateAsString != null) {
				reminderDate = new SimpleDateFormat(
						Constants.DATETIME_FORMAT_STRING).parse(dateAsString);
			}
		} catch (ParseException e) {
			Log.e(Constants.LOGTAG, CLASSNAME, e);
		}

		// Recurrence
		recurrenceType = parcel.readInt();
		recurrenceValue = parcel.readInt();
		nextOccurrenceId = parcel.readLong();

	}

	public Task clone() throws CloneNotSupportedException{
		Task task = (Task)super.clone();
		task.setId(0);
		task.setNextOccurrenceId(0);

		return task;
	}

	public Task() {
	}

	public boolean canHaveReminder() {
		return completed == false;
	}

	public Task createNextOccurrence(RecurrenceCalculations recurrences, TaskDateProvider dateProvider) {

		if(getTargetDate() == null) return null;
		if(getRecurrenceValue() == 0) return null;
		if(getTargetDate().after(dateProvider.getNow().getTime())) return null;

		Date nextOccurrenceTargetDate =  recurrences.create(this).getNext(getTargetDate(), dateProvider, getRecurrenceValue());

		if(nextOccurrenceTargetDate == null) return null;
		
		try {
			Task nextOccurrence = clone();
			nextOccurrence.setCompleted(false);
			nextOccurrence.setTargetDate(nextOccurrenceTargetDate);
			
			return nextOccurrence;
			
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	public void initializeReminders(ReminderCalculations reminders, TaskDateProvider dateProvider) {
		
		if(isCompleted()) return;

		Date now = new Date();

		if (reminderType == REMINDER_MANUAL) {
			reminderDate = targetDate;
		}

		if (targetDate == null) {
			if (reminderType != REMINDER_MANUAL) {
				reminderDate = reminders.create(this).getNext(dateProvider.getToday().getTime(), dateProvider, 1);
			}
		} else if (targetDate != null) {
			if (reminderType != REMINDER_MANUAL) {
				if (targetDate.before(now)) {
					reminderDate = reminders.create(this).getNext(targetDate, dateProvider, 1);
				} else if (targetDate == now | targetDate.after(now)) {
					reminderDate = targetDate;
				}
			}
		}
	}

	public void updateReminder(ReminderCalculations reminders, TaskDateProvider dateProvider) {
		reminderDate = reminders.create(this).getNext(targetDate == null ? 
				reminderDate : targetDate, dateProvider, 1);
	}
	
	public Date snooze(final TaskDateProvider dateProvider, int snoozeTimeInMinutes) {
		
		Calendar snoozedTimeCalendar = dateProvider.getNow();
		snoozedTimeCalendar.add(Calendar.MINUTE, snoozeTimeInMinutes);

		if (getReminderDate() != null) {
			if (snoozedTimeCalendar.getTime().before(getReminderDate()) == false) {
				return null;
			}
		}
		
		setSnoozeCount(getSnoozeCount() + 1);
		
		return snoozedTimeCalendar.getTime();
	}

	public void repeats(int recurrenceType, int recurrenceValue) {
		this.setRecurrenceType(recurrenceType);
		this.setRecurrenceValue(recurrenceValue);
	}

	public void complete() {
		reminderDate = null;
		setCompleted(true);
	}
}
