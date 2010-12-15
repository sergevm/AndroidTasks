package com.softwareprojects.androidtasks.unittest.reminders;

import java.util.Calendar;

import com.softwareprojects.androidtasks.domain.NotificationSource;
import com.softwareprojects.androidtasks.domain.RecurrenceCalculations;
import com.softwareprojects.androidtasks.domain.TaskDateCalculation;
import com.softwareprojects.androidtasks.domain.ReminderCalculations;
import com.softwareprojects.androidtasks.domain.Task;
import com.softwareprojects.androidtasks.domain.TaskAlarmManager;
import com.softwareprojects.androidtasks.domain.TaskDateProvider;
import com.softwareprojects.androidtasks.domain.TaskRepository;
import com.softwareprojects.androidtasks.domain.TaskScheduler;

import static org.mockito.Mockito.*;

import junit.framework.TestCase;

public class TaskSchedulerTest extends TestCase {

	private Task task;
	private TaskAlarmManager alarms;
	ReminderCalculations reminders;
	RecurrenceCalculations recurrences;
	TaskDateProvider dates;
	TaskDateCalculation reminder;
	TaskScheduler taskScheduler;
	TaskRepository taskRepository;
	
	Calendar now = Calendar.getInstance();

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		task = mock(Task.class);
		reminder = mock(TaskDateCalculation.class);
		dates = mock(TaskDateProvider.class);
		alarms = mock(TaskAlarmManager.class);
		reminders = mock(ReminderCalculations.class);
		recurrences = mock(RecurrenceCalculations.class);
		taskRepository = mock(TaskRepository.class);

		taskScheduler = new TaskScheduler(reminders, recurrences, alarms, dates, taskRepository);

		when(reminders.create(task)).thenReturn(reminder);
		when(dates.getNow()).thenReturn(now);
}
	
	public void test_WITH_a_reminder_set_and_no_targetdate_set_THEN_reminderdate_unchanged() {
	
		Calendar calendar = Calendar.getInstance();	
		calendar.add(Calendar.MINUTE, 20);
		task.setReminderDate(calendar.getTime());
		
		taskScheduler.snooze(task, 10, NotificationSource.ALARMSOURCE_REMINDERDATE);
		verify(task, times(1)).setReminderDate(calendar.getTime());
	}

	public void test_WITH_a_reminder_set_and_no_targetdate_set_THEN_alarmmanager_is_called() {
		
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MINUTE, 20);		
		task.setReminderDate(calendar.getTime());
		
		taskScheduler.snooze(task, 10, NotificationSource.ALARMSOURCE_REMINDERDATE);
		
		verify(alarms, times(1)).snoozeAlarm(task, now.getTime(), 10, NotificationSource.ALARMSOURCE_SNOOZE_REMINDERDATE);
	}

	public void test_WITH_a_targetdate_set_WHEN_reminder_type_manual_THEN_alarmmanager_is_called() {
		
		Calendar calendar = Calendar.getInstance();
		task.setTargetDate(calendar.getTime());
		task.initializeReminders(reminders, dates);
		
		taskScheduler.snooze(task, 10, NotificationSource.ALARMSOURCE_TARGETDATE);
		
		verify(alarms, times(1)).snoozeAlarm(task, now.getTime(), 10, NotificationSource.ALARMSOURCE_SNOOZE_TARGETDATE);
	}

	public void test_WITH_a_targetdate_set_WHEN_reminder_type_manual_THEN_targetdate_unchanged() {
		
		Calendar calendar = Calendar.getInstance();
		task.setTargetDate(calendar.getTime());
		task.initializeReminders(reminders, dates);
		
		taskScheduler.snooze(task, 10, NotificationSource.ALARMSOURCE_TARGETDATE);
		
		verify(task, times(1)).setTargetDate(calendar.getTime());
	}
}
