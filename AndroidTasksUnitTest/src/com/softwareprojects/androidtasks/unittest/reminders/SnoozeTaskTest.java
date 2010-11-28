package com.softwareprojects.androidtasks.unittest.reminders;

import java.util.Calendar;

import com.softwareprojects.androidtasks.domain.NotificationSource;
import com.softwareprojects.androidtasks.domain.Reminder;
import com.softwareprojects.androidtasks.domain.ReminderFactory;
import com.softwareprojects.androidtasks.domain.Task;
import com.softwareprojects.androidtasks.domain.TaskAlarmManager;
import com.softwareprojects.androidtasks.domain.TaskDateProvider;

import static org.mockito.Mockito.*;

import junit.framework.TestCase;

public class SnoozeTaskTest extends TestCase {

	private Task task;
	private TaskAlarmManager alarms;
	ReminderFactory reminders;
	TaskDateProvider dates;
	Reminder reminder;
	
	Calendar now = Calendar.getInstance();

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		task = new Task();
		reminder = mock(Reminder.class);
		dates = mock(TaskDateProvider.class);
		alarms = mock(TaskAlarmManager.class);
		reminders = mock(ReminderFactory.class);
		
		when(reminders.create(task)).thenReturn(reminder);
		when(dates.getNow()).thenReturn(now);
}
	
	public void test_WITH_a_reminder_set_and_no_targetdate_set_THEN_reminderdate_unchanged() {
	
		Calendar calendar = Calendar.getInstance();	
		calendar.add(Calendar.MINUTE, 20);
		task.setReminderDate(calendar.getTime());
		
		task.snooze(alarms, dates, 10, NotificationSource.ALARMSOURCE_REMINDERDATE);
		
		assertEquals(calendar.getTime(), task.getReminderDate());
	}

	public void test_WITH_a_reminder_set_and_no_targetdate_set_THEN_alarmmanager_is_called() {
		
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MINUTE, 20);		
		task.setReminderDate(calendar.getTime());
		
		task.snooze(alarms, dates, 10, NotificationSource.ALARMSOURCE_REMINDERDATE);
		
		verify(alarms, times(1)).snoozeAlarm(task, 10, NotificationSource.ALARMSOURCE_SNOOZE_REMINDERDATE);
	}

	public void test_WITH_a_targetdate_set_WHEN_reminder_type_manual_THEN_alarmmanager_is_called() {
		
		Calendar calendar = Calendar.getInstance();
		task.setTargetDate(calendar.getTime());
		task.initialize(alarms, reminders, dates);
		
		task.snooze(alarms, dates, 10, NotificationSource.ALARMSOURCE_TARGETDATE);
		
		verify(alarms, times(1)).snoozeAlarm(task, 10, NotificationSource.ALARMSOURCE_SNOOZE_TARGETDATE);
	}

	public void test_WITH_a_targetdate_set_WHEN_reminder_type_manual_THEN_targetdate_unchanged() {
		
		Calendar calendar = Calendar.getInstance();
		task.setTargetDate(calendar.getTime());
		task.initialize(alarms, reminders, dates);
		
		task.snooze(alarms, dates, 10, NotificationSource.ALARMSOURCE_TARGETDATE);
		
		assertEquals(calendar.getTime(), task.getTargetDate());
	}
}
