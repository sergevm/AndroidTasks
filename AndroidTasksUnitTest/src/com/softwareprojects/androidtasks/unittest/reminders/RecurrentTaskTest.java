package com.softwareprojects.androidtasks.unittest.reminders;


import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Calendar;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.softwareprojects.androidtasks.domain.RecurrenceCalculations;
import com.softwareprojects.androidtasks.domain.Task;
import com.softwareprojects.androidtasks.domain.TaskAlarmManager;
import com.softwareprojects.androidtasks.domain.TaskDateProvider;
import com.softwareprojects.androidtasks.domain.dates.DaysCalculation;

public class RecurrentTaskTest {

	Task task;
	Calendar now;
	TaskDateProvider dateProvider;
	TaskAlarmManager alarmManager;
	RecurrenceCalculations recurrences;
	
	@Before
	public void setUp() throws Exception {
		
		now = Calendar.getInstance();
		now.set(Calendar.YEAR, 2010);
		now.set(Calendar.MONTH, 11);
		now.set(Calendar.DATE, 5);
		now.set(Calendar.HOUR, 0);
		now.set(Calendar.MINUTE, 0);
		now.set(Calendar.SECOND, 0);
		now.set(Calendar.MILLISECOND, 0);
		
		task = new Task();
		
		dateProvider = mock(TaskDateProvider.class);
		when(dateProvider.getToday()).thenReturn(now);
		when(dateProvider.getNow()).thenReturn(now);
	
		recurrences = mock(RecurrenceCalculations.class);
		// TODO: REAL INSTANCE !!!!!!!!!!!!!!!!!!!!!!!!!!
		when(recurrences.create(task)).thenReturn(new DaysCalculation());
		
		alarmManager = mock(TaskAlarmManager.class);
	}

	@Test
	public void WITH_a_targetdate_set_in_future_WHEN_daily_recurrence_THEN_alarmmanager_is_not_set() {

		Calendar targetDate = Calendar.getInstance();
		targetDate.setTime(now.getTime());
		targetDate.add(Calendar.DATE, 7);
		
		task.setTargetDate(targetDate.getTime());
		task.repeats(Task.REPEAT_INTERVAL_DAYS, 1);
		
		task.createNextOccurrence(recurrences, dateProvider);
		
		Assert.assertEquals(task.getRecurrenceType(), Task.REPEAT_INTERVAL_DAYS);
		Assert.assertEquals(task.getRecurrenceValue(), 1);
		
		targetDate.add(Calendar.DATE, 1);
				
		verify(alarmManager, never()).setRecurrent(task, targetDate.getTime());
	}
	
	@Test
	public void WITH_a_targetdate_set_in_past_WHEN_daily_recurrence_THEN_alarm_set_for_next_day() {

		Calendar targetDate = Calendar.getInstance();
		targetDate.setTime(now.getTime());
		targetDate.add(Calendar.DATE, -4);
		
		task.setTargetDate(targetDate.getTime());
		task.repeats(Task.REPEAT_INTERVAL_DAYS, 1);
		
		Task nextOccurrence = task.createNextOccurrence(recurrences, dateProvider);
		
		Calendar expected = (Calendar)now.clone();
		expected.set(Calendar.HOUR, targetDate.get(Calendar.HOUR));
		expected.set(Calendar.MINUTE, targetDate.get(Calendar.MINUTE));
		expected.set(Calendar.SECOND, targetDate.get(Calendar.SECOND));
		expected.set(Calendar.MILLISECOND, targetDate.get(Calendar.MILLISECOND));
		
		expected.add(Calendar.DATE, 1);

		verify(recurrences, times(1)).create(task);
		Assert.assertEquals(expected.getTime(), nextOccurrence.getTargetDate());
	}
}
