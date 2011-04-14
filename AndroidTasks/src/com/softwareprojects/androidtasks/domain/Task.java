package com.softwareprojects.androidtasks.domain;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.softwareprojects.androidtasks.Constants;

public class Task implements Parcelable, Cloneable {
	private long id;
	private Date createDate;
	private Date modificationDate;
	private Date targetDate;
	private String description;
	private boolean completed;
	private boolean deleted;
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
		if (this.getReminderType() == REMINDER_MANUAL) {
			setReminderDate(null);
		}
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

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
	}

	public Date getModificationDate() {
		return modificationDate;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public boolean isDeleted() {
		return deleted;
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

		// create date
		ParcelReaderWriter.writeDateToParcel(dest, createDate);

		// modification date
		ParcelReaderWriter.writeDateToParcel(dest, modificationDate);

		// target date
		ParcelReaderWriter.writeDateToParcel(dest, targetDate);

		// destination
		dest.writeString(description);

		// completed
		dest.writeBooleanArray(new boolean[] { completed, deleted });

		// snoozeCount
		dest.writeInt(snoozeCount);

		// notes
		dest.writeString(notes);

		// location
		dest.writeString(location);

		// reminder
		dest.writeInt(reminderType);

		// reminderDate
		ParcelReaderWriter.writeDateToParcel(dest, reminderDate);

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

		// create date
		try {
			createDate = ParcelReaderWriter.readDateFromParcel(parcel);
		} catch (ParseException e) {
			Log.e(Constants.LOGTAG, CLASSNAME, e);
		}
		// modification date
		try {
			modificationDate = ParcelReaderWriter.readDateFromParcel(parcel);
		} catch (ParseException e) {
			Log.e(Constants.LOGTAG, CLASSNAME, e);
		}

		// target date
		try {
			targetDate = ParcelReaderWriter.readDateFromParcel(parcel);
		} catch (ParseException e) {
			Log.e(Constants.LOGTAG, CLASSNAME, e);
		}

		// description
		description = parcel.readString();

		// Boolean values grouped in a single array
		boolean[] buffer = parcel.createBooleanArray();
		completed = buffer[0];
		deleted = buffer[1];

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
			reminderDate = ParcelReaderWriter.readDateFromParcel(parcel);
		} catch (ParseException e) {
			Log.e(Constants.LOGTAG, CLASSNAME, e);
		}

		// Recurrence
		recurrenceType = parcel.readInt();
		recurrenceValue = parcel.readInt();
		nextOccurrenceId = parcel.readLong();

	}

	public Task clone() throws CloneNotSupportedException {
		Task task = (Task) super.clone();

		Date cloneDate = new Date();

		task.setId(0);

		task.setDeleted(false);
		task.setCreateDate(cloneDate);
		task.setModificationDate(cloneDate);

		task.setNextOccurrenceId(0);

		return task;
	}

	public Task() {
	}

	public boolean canHaveReminder() {
		return completed == false;
	}

	/**
	 * Creates the next instance of a recurrent task, given that:
	 * <ul>
	 * <li>The task has a target date</li>
	 * <li>The task has a recurrence set</li>
	 * </ul>
	 * 
	 * The next instance is a clone of this instance, with a target date that 
	 * is calculated with this tasks' target date as an offset, and such that 
	 * the target date of the returned tasks is in the future and later than 
	 * the target date of the current task.
	 * 
	 * @param recurrences Factory for recurrent task date calculations
	 * @param dateProvider Provides date bound info such as the current time, today, ...
	 * @return
	 */
	public Task createNextInstance(RecurrenceCalculations recurrences, TaskDateProvider dateProvider) {

		if (getTargetDate() == null) return null;
		if (getRecurrenceValue() == 0) return null;

		// TODO: the target date should always be bigger than the current instances' target date !!!!!
		Date nextInstanceTargetDate = recurrences.create(this).
			getNext(getTargetDate(), dateProvider, getRecurrenceValue());

		if (nextInstanceTargetDate == null)
			return null;

		try {
			Task nextInstance = clone();
			nextInstance.setCompleted(false);
			nextInstance.setTargetDate(nextInstanceTargetDate);

			return nextInstance;

		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Returns whether the target date of the Task instance is in the future.
	 * 
	 * @param dateProvider Provides access to date bound info such as current time
	 * @return
	 */
	public boolean hasTargetDateInFuture(TaskDateProvider dateProvider) {
		
		if (getTargetDate() == null) {
			return false;
		}
		
		return getTargetDate().after(dateProvider.getNow().getTime());
	}

	/**
	 * (Re)calculates the first expected reminder date on the task. Neglects the current value of 
	 * the reminder date, in contrast with the nextReminder() method, which explicitly expects the
	 * reminder date to be set on the current task instance!!!!!
	 * 
	 @param reminders Reminder calculation factory. This factory decides which calculator to use 
	 		based on the characteristics of the current Task instance
	 @param dateProvider Provides access to the current time, today, etc.
	 */
	public void initializeReminders(ReminderCalculations reminders, TaskDateProvider dateProvider) {

		if (isCompleted()) {
			
			setReminderDate(null);
			return;
		}

		Date now = dateProvider.getNow().getTime();

		if (reminderType == REMINDER_MANUAL) {
			
			if (targetDate != null && targetDate.after(now)) {
				reminderDate = targetDate;
			} else {
				reminderDate = null;
			}
			
			return;
		}

		if (targetDate == null) {
						
			// No target date, and yet a reminder type set => create a reminder 
			// that is calculated for that type, with the current time as offset
			reminderDate = reminders.create(this).getNext(now, dateProvider, 1);
			
		} else {

			if (targetDate.before(now)) {
				
				// Set the first reminder to a valid multiple of the reminder type basic 
				// unit, valid meaning that the reminder time is in the future
				reminderDate = reminders.create(this).getNext(targetDate, dateProvider, 1);
				
			} else {
				
				// Set the first reminder to the target date
				reminderDate = targetDate;
				
			}
		}
	}

	/**
	 * Sets a new reminder date on an this instance. If the task has a target date, than that one 
	 * is used to calculate the next reminder time. If there is no target date on the task, then 
	 * the current instances' reminder date is used as the basis for the calculation. 
	 * 
	 * <i>Note that the current reminder date is taken into account when no target date is set!</i>
	 * 
	 * @param reminders Factory that chooses the correct reminder time calculator based on the task
	 * @param dateProvider Provides access to date bound info, such as current time, ...
	 */
	public void nextReminder(ReminderCalculations reminders, TaskDateProvider dateProvider) {

		if (isCompleted() || isDeleted()) {
			
			setReminderDate(null);
			return;
		}

		reminderDate = reminders.create(this).getNext(targetDate == null ? 
				reminderDate : targetDate, dateProvider, 1);
	}

	/**
	 * Calculates the target date for the snooze action, and returns the time that the 
	 * a notification for the current reminder should be displayed. Validates that that 
	 * target date does not cross the next reminder date that has been set for this 
	 * instance; if it does, then null is returned.<br/><br/>
	 * 
	 * @param dateProvider Provides access to date related info such as the current time, ...
	 * @param snoozeTimeInMinutes The time in minutes that the reminder should be snoozed.
	 * @return
	 */
	public Date snooze(final TaskDateProvider dateProvider, int snoozeTimeInMinutes) {

		Calendar snoozeTargetTime = dateProvider.getNow();
		snoozeTargetTime.add(Calendar.MINUTE, snoozeTimeInMinutes);

		if (getReminderDate() != null) {
			
			if (snoozeTargetTime.getTime().after(getReminderDate())) {
				return null;
			}
		}
		
		if(snoozeTargetTime.before(dateProvider.getNow())){
			return null;
		}

		setSnoozeCount(getSnoozeCount() + 1);

		return snoozeTargetTime.getTime();
	}

	public void repeats(int recurrenceType, int recurrenceValue) {
		
		this.setRecurrenceType(recurrenceType);
		this.setRecurrenceValue(recurrenceValue);
	}

	public void complete() {
		
		reminderDate = null;
		setCompleted(true);
	}

	public boolean isRecurrent() {
		
		return getRecurrenceType() != REPEAT_NONE;
	}
}
