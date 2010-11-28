package com.softwareprojects.androidtasks.unittest.reminders;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Calendar;
import java.util.Date;

import junit.framework.TestCase;

import com.softwareprojects.androidtasks.domain.Reminder;
import com.softwareprojects.androidtasks.domain.ReminderFactory;
import com.softwareprojects.androidtasks.domain.Task;
import com.softwareprojects.androidtasks.domain.TaskAlarmManager;
import com.softwareprojects.androidtasks.domain.TaskDateFormatter;
import com.softwareprojects.androidtasks.domain.TaskDateProvider;

public class EditTaskTest extends TestCase {

	Task task;
	TaskAlarmManager alarms;
	TaskDateProvider dates;
	ReminderFactory reminders;
	Reminder reminder;
	Calendar today;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		task = new Task();
		alarms = mock(TaskAlarmManager.class);
		reminder = mock(Reminder.class);
		dates = mock(TaskDateProvider.class);
		reminders = mock(ReminderFactory.class);
		
		today = Calendar.getInstance();
		today.setTime(TaskDateFormatter.getToday());
		
		when(reminders.create(task)).thenReturn(reminder);
		when(dates.getToday()).thenReturn(today);
	}
	
	public void test_reminderdate_stays_null_if_no_targetdate_set() {
		task.initialize(alarms, reminders, dates);
		assertNull(task.getReminderDate());
	}
	
	public void test_remindertype_is_manual_by_default() {
		assertEquals(Task.REMINDER_MANUAL, task.getReminder());
	}
	
	public void test_reminderdate_equals_targetdate_by_default() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, 1);
		task.setTargetDate(calendar.getTime());
		
		task.initialize(alarms, reminders, dates);
		
		assertEquals(task.getTargetDate(), task.getReminderDate());
	}
	
	public void test_WHEN_no_targetdate_and_remindertype_weekly_THEN_reminderdate_set_nextweek() {
		task.setReminder(Task.REMINDER_WEEKLY);
		
		Calendar calendar = (Calendar)today.clone();
		calendar.add(Calendar.DATE, 7);

		when(reminder.getNextReminder(today.getTime(), dates)).thenReturn(calendar.getTime());

		task.initialize(alarms, reminders, dates);
		
		verify(reminder, times(1)).getNextReminder(any(Date.class), any(TaskDateProvider.class));
		assertEquals(calendar.getTime(), task.getReminderDate());
	}
	
	public void test_WHEN_no_targetdate_and_remindertype_weekly_THEN_reminderdate_set_nextweek_without_time_information() {	
		task.setReminder(Task.REMINDER_WEEKLY);
	
		Calendar calendar = (Calendar)today.clone();
		calendar.add(Calendar.DATE, 7);
		
		when(reminder.getNextReminder(today.getTime(), dates)).thenReturn(calendar.getTime());
		
		task.initialize(alarms, reminders, dates);
				
		assertEquals(calendar.getTime(), task.getReminderDate());
	}
	
	
	public void test_WITH_past_reminderdate_and_weekly_remindertype_WHEN_targetdate_is_null_THEN_reminderdate_moves_to_week_after_today() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, -1);
		
		task.setReminder(Task.REMINDER_WEEKLY);
		task.setReminderDate(calendar.getTime());

		calendar.setTime(TaskDateFormatter.getToday());
		calendar.add(Calendar.DATE, 7);

		when(reminder.getNextReminder(any(Date.class), any(TaskDateProvider.class))).thenReturn(calendar.getTime());

		task.initialize(alarms, reminders, dates);
		
		verify(reminder, times(1)).getNextReminder(any(Date.class), any(TaskDateProvider.class));
		assertEquals(calendar.getTime(), task.getReminderDate());
	}

	public void test_WHEN_past_reminderdate_and_past_targetdate_and_weekly_remindertype__THEN_reminderdate_moves_to_next_week() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, -1);
		
		task.setReminder(Task.REMINDER_WEEKLY);
		task.setReminderDate(calendar.getTime());
		task.setTargetDate(calendar.getTime());

		calendar.add(Calendar.DATE, 7);
		when(reminder.getNextReminder(task.getTargetDate(), dates)).thenReturn(calendar.getTime());

		task.initialize(alarms, reminders, dates);
				
		verify(reminder, times(1)).getNextReminder(any(Date.class), any(TaskDateProvider.class));
		assertEquals(calendar.getTime(), task.getReminderDate());
	}
	
	public void test_WHEN_past_targetdate_and_future_reminderdate_and_weekly_remindertype_THEN_reminderdate_remains_unchanged_and_future() {
		Calendar calendar = Calendar.getInstance();
		
		task.setReminder(Task.REMINDER_WEEKLY);
		calendar.add(Calendar.DATE, -1);
		task.setTargetDate(calendar.getTime());

		calendar.add(Calendar.DATE, 7);
		task.setReminderDate(calendar.getTime());

		when(reminder.getNextReminder(task.getTargetDate(), dates)).thenReturn(calendar.getTime());

		task.initialize(alarms, reminders, dates);
			
		verify(reminder, times(1)).getNextReminder(any(Date.class), any(TaskDateProvider.class));
		assertEquals(calendar.getTime(), task.getReminderDate());
	}
	
	public void test_WITH_future_targetdate_and_reminderdate_and_remindertype_weekly_WHEN_dates_are_equal_THEN_reminderdate_stays_targetdate() {
		task.setReminder(Task.REMINDER_WEEKLY);
		
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, 1);
		
		task.setTargetDate(calendar.getTime());
		task.setReminderDate(calendar.getTime());
		task.initialize(alarms, reminders, dates);
		
		assertEquals(task.getTargetDate(), task.getReminderDate());
	}

	public void test_WITH_future_targetdate_and_reminderdate_and_remindertype_weekly_WHEN_reminderdate_before_targetdate_THEN_reminderdate_set_to_targetdate() {
		Calendar calendar = Calendar.getInstance();
		
		task.setReminder(Task.REMINDER_WEEKLY);
		calendar.add(Calendar.DATE, 2);
		task.setTargetDate(calendar.getTime());

		calendar.add(Calendar.DATE, -1);
		task.setReminderDate(calendar.getTime());
		
		task.initialize(alarms, reminders, dates);
			
		assertEquals(task.getTargetDate(), task.getReminderDate());
	}
	
	public void test_WITH_equal_future_targetdate_and_reminderdate_WHEN_targetdate_set_to_earlier_future_date_THEN_reminderdate_set_to_targetdate() {
		Calendar calendar = Calendar.getInstance();
		
		task.setReminder(Task.REMINDER_WEEKLY);
		calendar.add(Calendar.DATE, 2);

		task.setTargetDate(calendar.getTime());
		task.setReminderDate(calendar.getTime());
		
		calendar.add(Calendar.DATE, -1);
		task.setTargetDate(calendar.getTime());
		
		task.initialize(alarms, reminders, dates);
			
		assertEquals(task.getTargetDate(), task.getReminderDate());
		
	}
}