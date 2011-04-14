package taskschedulertests.scheduling;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import taskschedulertests.TaskSchedulerTestBase;

import com.softwareprojects.androidtasks.domain.Task;


public class When_scheduling_new_task extends TaskSchedulerTestBase {
	
	@Before public void setUp() throws Exception {
		super.setUp();
	}
	
	@Test public void Then_the_task_should_be_persisted() {
		
		when(task.getId()).thenReturn(0l);
		taskScheduler.schedule(task);
		
		verify(taskRepository, times(1)).insert(task);
	}
	
	@Test public void Then_the_task_should_initialize_reminders() {
		taskScheduler.schedule(task);
		
		verify(task, times(1)).initializeReminders(reminders, dates);
	}
	
	@Test public void If_recurrent_then_the_repository_should_be_checked_for_existing_reoccurrence() {
		
		when(task.isRecurrent()).thenReturn(true);
		
		taskScheduler.schedule(task);
		verify(taskRepository, times(1)).findNextOccurrenceOf(task);
	}
	
	@Test public void If_not_recurrent_then_the_repository_should_not_be_checked_for_existing_reoccurrence() {
		
		taskScheduler.schedule(task);
		verify(taskRepository, never()).findNextOccurrenceOf(task);
	}
	
	@Test public void Then_an_alarm_should_be_set_for_a_non_null_reminderdate() {

		Date reminderDate = createFutureDate();

		when(task.getReminderDate()).thenReturn(reminderDate);
	
		taskScheduler.schedule(task);
		verify(alarms, atLeastOnce()).resetReminderNotificationAlarm(task);
	}
	
	@Test public void Then_no_alarm_should_be_set_if_the_task_has_no_reminder_date() {

		taskScheduler.schedule(task);
		verify(alarms, never()).resetReminderNotificationAlarm(any(Task.class));
	}
}