package com.softwareprojects.androidtasks.unittest.reminders;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Calendar;
import java.util.Date;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.softwareprojects.androidtasks.domain.RecurrenceCalculations;
import com.softwareprojects.androidtasks.domain.ReminderCalculations;
import com.softwareprojects.androidtasks.domain.Task;
import com.softwareprojects.androidtasks.domain.TaskAlarmManager;
import com.softwareprojects.androidtasks.domain.TaskDateCalculation;
import com.softwareprojects.androidtasks.domain.TaskDateFormatter;
import com.softwareprojects.androidtasks.domain.TaskDateProvider;

public class EditTaskTest {

	Task task;
	TaskAlarmManager alarms;
	TaskDateProvider dates;
	ReminderCalculations reminders;
	RecurrenceCalculations recurrences;
	TaskDateCalculation reminder;
	Calendar today;
	
	@Before
	public void setUp() throws Exception {
		
		task = new Task();
		alarms = mock(TaskAlarmManager.class);
		reminder = mock(TaskDateCalculation.class);
		dates = mock(TaskDateProvider.class);
		reminders = mock(ReminderCalculations.class);
		recurrences = mock(RecurrenceCalculations.class);
		
		today = Calendar.getInstance();
		today.setTime(TaskDateFormatter.getToday());
		
		when(reminders.create(task)).thenReturn(reminder);
		when(dates.getToday()).thenReturn(today);
	}
	
	@Test
	public void reminderdate_stays_null_if_no_targetdate_set() {
		task.initializeReminders(reminders, dates);
		Assert.assertNull(task.getReminderDate());
	}
	
	@Test
	public void remindertype_is_manual_by_default() {
		Assert.assertEquals(Task.REMINDER_MANUAL, task.getReminderType());
	}
	
	@Test
	public void reminderdate_equals_targetdate_by_default() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, 1);
		task.setTargetDate(calendar.getTime());
		
		task.initializeReminders(reminders, dates);
		
		Assert.assertEquals(task.getTargetDate(), task.getReminderDate());
	}
	
	@Test
	public void WHEN_no_targetdate_and_remindertype_weekly_THEN_reminderdate_set_nextweek() {
		task.setReminderType(Task.REMINDER_WEEKLY);
		
		Calendar calendar = (Calendar)today.clone();
		calendar.add(Calendar.DATE, 7);

		when(reminder.getNext(today.getTime(), dates, 1)).thenReturn(calendar.getTime());

		task.initializeReminders(reminders, dates);
		
		verify(reminder, times(1)).getNext(any(Date.class), any(TaskDateProvider.class), eq(1));
		Assert.assertEquals(calendar.getTime(), task.getReminderDate());
	}
	
	@Test
	public void WHEN_no_targetdate_and_remindertype_weekly_THEN_reminderdate_set_nextweek_without_time_information() {	
		task.setReminderType(Task.REMINDER_WEEKLY);
	
		Calendar calendar = (Calendar)today.clone();
		calendar.add(Calendar.DATE, 7);
		
		when(reminder.getNext(today.getTime(), dates, 1)).thenReturn(calendar.getTime());
		task.initializeReminders(reminders, dates);
				
		Assert.assertEquals(calendar.getTime(), task.getReminderDate());
	}
	
	
	@Test
	public void WITH_past_reminderdate_and_weekly_remindertype_WHEN_targetdate_is_null_THEN_reminderdate_moves_to_week_after_today() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, -1);
		
		task.setReminderType(Task.REMINDER_WEEKLY);
		task.setReminderDate(calendar.getTime());

		calendar.setTime(TaskDateFormatter.getToday());
		calendar.add(Calendar.DATE, 7);

		when(reminder.getNext(any(Date.class), any(TaskDateProvider.class), eq(1))).thenReturn(calendar.getTime());

		task.initializeReminders(reminders, dates);
		
		verify(reminder, times(1)).getNext(any(Date.class), any(TaskDateProvider.class), eq(1));
		Assert.assertEquals(calendar.getTime(), task.getReminderDate());
	}

	@Test
	public void WHEN_past_reminderdate_and_past_targetdate_and_weekly_remindertype__THEN_reminderdate_moves_to_next_week() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, -1);
		
		task.setReminderType(Task.REMINDER_WEEKLY);
		task.setReminderDate(calendar.getTime());
		task.setTargetDate(calendar.getTime());

		calendar.add(Calendar.DATE, 7);
		when(reminder.getNext(task.getTargetDate(), dates, 1)).thenReturn(calendar.getTime());

		task.initializeReminders(reminders, dates);
				
		verify(reminder, times(1)).getNext(any(Date.class), any(TaskDateProvider.class), eq(1));
		Assert.assertEquals(calendar.getTime(), task.getReminderDate());
	}
	
	@Test
	public void WHEN_past_targetdate_and_future_reminderdate_and_weekly_remindertype_THEN_reminderdate_remains_unchanged_and_future() {
		Calendar calendar = Calendar.getInstance();
		
		task.setReminderType(Task.REMINDER_WEEKLY);
		calendar.add(Calendar.DATE, -1);
		task.setTargetDate(calendar.getTime());

		calendar.add(Calendar.DATE, 7);
		task.setReminderDate(calendar.getTime());

		when(reminder.getNext(task.getTargetDate(), dates, 1)).thenReturn(calendar.getTime());

		task.initializeReminders(reminders, dates);
			
		verify(reminder, times(1)).getNext(any(Date.class), any(TaskDateProvider.class), eq(1));
		Assert.assertEquals(calendar.getTime(), task.getReminderDate());
	}
	
	@Test
	public void WITH_future_targetdate_and_reminderdate_and_remindertype_weekly_WHEN_dates_are_equal_THEN_reminderdate_stays_targetdate() {
		task.setReminderType(Task.REMINDER_WEEKLY);
		
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, 1);
		
		task.setTargetDate(calendar.getTime());
		task.setReminderDate(calendar.getTime());
		task.initializeReminders(reminders, dates);
		
		Assert.assertEquals(task.getTargetDate(), task.getReminderDate());
	}

	@Test
	public void WITH_future_targetdate_and_reminderdate_and_remindertype_weekly_WHEN_reminderdate_before_targetdate_THEN_reminderdate_set_to_targetdate() {
		Calendar calendar = Calendar.getInstance();
		
		task.setReminderType(Task.REMINDER_WEEKLY);
		calendar.add(Calendar.DATE, 2);
		task.setTargetDate(calendar.getTime());

		calendar.add(Calendar.DATE, -1);
		task.setReminderDate(calendar.getTime());
		
		task.initializeReminders(reminders, dates);
			
		Assert.assertEquals(task.getTargetDate(), task.getReminderDate());
	}
	
	@Test
	public void WITH_equal_future_targetdate_and_reminderdate_WHEN_targetdate_set_to_earlier_future_date_THEN_reminderdate_set_to_targetdate() {
		Calendar calendar = Calendar.getInstance();
		
		task.setReminderType(Task.REMINDER_WEEKLY);
		calendar.add(Calendar.DATE, 2);

		task.setTargetDate(calendar.getTime());
		task.setReminderDate(calendar.getTime());
		
		calendar.add(Calendar.DATE, -1);
		task.setTargetDate(calendar.getTime());
		
		task.initializeReminders(reminders, dates);
			
		Assert.assertEquals(task.getTargetDate(), task.getReminderDate());
		
	}
}