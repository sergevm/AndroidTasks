package com.softwareprojects.androidtasks;

import java.util.Calendar;

import junit.framework.TestCase;

import com.softwareprojects.androidtasks.domain.Task;

public class TaskTest extends TestCase {
	
	public void test_reminderdate_stays_null_if_no_targetdate_set() {
		Task task = new Task();
		task.set(new TestTaskAlarmManager());
		
		assertNull(task.getReminderDate());
	}
	
	public void test_remindertype_is_manual_by_default() {
		Task task = new Task();
		
		assertEquals(Task.REMINDER_MANUAL, task.getReminder());
	}
	
	public void test_reminderdate_equals_targetdate_by_default() {
		Task task = new Task();
		
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, 1);
		task.setTargetDate(calendar.getTime());
		
		task.set(new TestTaskAlarmManager());
		
		assertEquals(task.getTargetDate(), task.getReminderDate());
	}
	
	public void test_WHEN_no_targetdate_and_remindertype_weekly_THEN_reminderdate_set_nextweek() {
		Task task = new Task();
		task.setReminder(Task.REMINDER_WEEKLY);
		task.set(new TestTaskAlarmManager());
		
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, 6);
		
		assertTrue(task.getReminderDate().after(calendar.getTime()));
		
		calendar.add(Calendar.DATE, 1);
		
		assertTrue(task.getReminderDate().before(calendar.getTime()));
	}
	
	public void test_WITH_future_targetdate_and_remindertype_weekly_THEN_reminderdate_equals_targetdate() {
		Task task = new Task();
		task.setReminder(Task.REMINDER_WEEKLY);
		
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, 1);
		
		task.setTargetDate(calendar.getTime());
		
		task.set(new TestTaskAlarmManager());
		
		assertEquals(task.getTargetDate(), task.getReminderDate());
	}
	
	public void test_WITH_past_reminderdate_and_weekly_remindertype_WHEN_targetdate_is_null_THEN_reminderdate_moves_to_next_week() {
		Task task = new Task();
		
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, -1);
		
		task.setReminder(Task.REMINDER_WEEKLY);
		task.setReminderDate(calendar.getTime());
		
		task.set(new TestTaskAlarmManager());

		calendar.add(Calendar.DATE, 7);
				
		assertEquals(calendar.getTime(), task.getReminderDate());
	}
}