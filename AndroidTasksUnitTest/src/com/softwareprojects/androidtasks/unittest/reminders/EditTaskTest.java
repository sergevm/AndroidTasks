package com.softwareprojects.androidtasks.unittest.reminders;

import static org.mockito.Mockito.mock;

import java.util.Calendar;

import junit.framework.TestCase;

import com.softwareprojects.androidtasks.domain.Task;
import com.softwareprojects.androidtasks.domain.TaskAlarmManager;

public class EditTaskTest extends TestCase {

	Task task;
	TaskAlarmManager alarmManager;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		task = new Task();
		alarmManager = mock(TaskAlarmManager.class);
	}
	
	public void test_reminderdate_stays_null_if_no_targetdate_set() {
		task.set(alarmManager);
		assertNull(task.getReminderDate());
	}
	
	public void test_remindertype_is_manual_by_default() {
		assertEquals(Task.REMINDER_MANUAL, task.getReminder());
	}
	
	public void test_reminderdate_equals_targetdate_by_default() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, 1);
		task.setTargetDate(calendar.getTime());
		
		task.set(alarmManager);
		
		assertEquals(task.getTargetDate(), task.getReminderDate());
	}
	
	public void test_WHEN_no_targetdate_and_remindertype_weekly_THEN_reminderdate_set_nextweek() {
		task.setReminder(Task.REMINDER_WEEKLY);
		task.set(alarmManager);
		
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, 6);
		
		assertTrue(task.getReminderDate().after(calendar.getTime()));
		
		calendar.add(Calendar.DATE, 1);
		
		assertTrue(task.getReminderDate().before(calendar.getTime()));
	}
	
	public void test_WHEN_no_targetdate_and_remindertype_weekly_THEN_reminderdate_set_nextweek_without_time_information() {
		task.setReminder(Task.REMINDER_WEEKLY);
		task.set(alarmManager);
		
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, 7);
		calendar.set(Calendar.HOUR, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		
		assertEquals(calendar.getTime(), task.getReminderDate());
	}
	
	public void test_WITH_future_targetdate_and_reminderdate_and_remindertype_weekly_THEN_reminderdate_equals_targetdate() {
		task.setReminder(Task.REMINDER_WEEKLY);
		
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, 1);
		
		task.setTargetDate(calendar.getTime());
		task.setReminderDate(calendar.getTime());
		task.set(alarmManager);
		
		assertEquals(task.getTargetDate(), task.getReminderDate());
	}
	
	public void test_WITH_past_reminderdate_and_weekly_remindertype_WHEN_targetdate_is_null_THEN_reminderdate_moves_to_next_week() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, -1);
		
		task.setReminder(Task.REMINDER_WEEKLY);
		task.setReminderDate(calendar.getTime());
		
		task.set(alarmManager);

		calendar.add(Calendar.DATE, 7);
				
		assertEquals(calendar.getTime(), task.getReminderDate());
	}

	public void test_WITH_past_reminderdate_and_weekly_remindertype_WHEN_reminderdate_equals_targetdate_THEN_reminderdate_moves_to_next_week() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, -1);
		
		task.setReminder(Task.REMINDER_WEEKLY);
		task.setReminderDate(calendar.getTime());
		task.setTargetDate(calendar.getTime());
		
		task.set(alarmManager);

		calendar.add(Calendar.DATE, 7);
				
		assertEquals(calendar.getTime(), task.getReminderDate());
	}
	
	public void test_WHEN_past_targetdate_and_future_reminderdate_and_weekly_remindertype_THEN_reminderdate_remains_unchanged() {
		Calendar calendar = Calendar.getInstance();
		
		task.setReminder(Task.REMINDER_WEEKLY);
		calendar.add(Calendar.DATE, -1);
		task.setTargetDate(calendar.getTime());

		calendar.add(Calendar.DATE, 2);
		task.setReminderDate(calendar.getTime());
		
		task.set(alarmManager);
			
		assertEquals(calendar.getTime(), task.getReminderDate());
	}
}