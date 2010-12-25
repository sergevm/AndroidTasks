package taskschedulertests.scheduling;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import taskschedulertests.TaskSchedulerTestBase;


public class When_scheduling_new_task extends TaskSchedulerTestBase {
	
	Date reminderDate;
	
	@Before public void setUp() throws Exception {
		super.setUp();
		reminderDate = createFutureDate();
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
	
	@Test public void Then_the_repository_should_be_checked_for_existing_reoccurrence() {
		taskScheduler.schedule(task);
		
		verify(taskRepository, times(1)).getNextOccurrenceOf(task);
	}
	
	@Test public void Then_an_alarm_should_be_set_for_a_non_null_reminderdate() {

		when(task.getReminderDate()).thenReturn(reminderDate);
		taskScheduler.schedule(task);
		verify(alarms, atLeastOnce()).setTarget(task, reminderDate);
	}
}