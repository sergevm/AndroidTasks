package taskschedulertests.recurrent;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import com.softwareprojects.androidtasks.domain.Task;

import static org.mockito.Mockito.*;
import taskschedulertests.TaskSchedulerTestBase;


public class Given_next_occurrence_exists extends TaskSchedulerTestBase{
	Task next;
	Date nextOccurrenceDate;
		
	@Before public void Setup() {
		
		nextOccurrenceDate = createFutureDate();
		
		next = mock(Task.class);
		when(next.getReminderDate()).thenReturn(nextOccurrenceDate);
		when(task.createNextOccurrence(recurrences, dates)).thenReturn(next);
	}
	
	@Test public void Then_the_repository_is_first_checked_for_a_next_occurrence() {
		
		taskScheduler.initializeNextOccurrence(task);
		verify(taskRepository, atLeastOnce()).findNextOccurrenceOf(task);
	}
	
	@Test public void Then_reminders_should_be_initialized_on_next_occurrence() {
				
		taskScheduler.initializeNextOccurrence(task);
		verify(next, atLeastOnce()).initializeReminders(reminders, dates);
	}
	
	@Test public void When_no_targetdate_exists_on_current_task_then_a_reminder_alarm_should_be_set() {
		
		taskScheduler.initializeNextOccurrence(task);
		verify(alarms, atLeastOnce()).setReminder(next);
	}
	
	@Test public void When_a_targetdate_exists_on_current_task_then_a_target_alarm_should_be_set() {
		
		when(next.getTargetDate()).thenReturn(nextOccurrenceDate);
		taskScheduler.initializeNextOccurrence(task);
		verify(alarms, atLeastOnce()).setTarget(next, nextOccurrenceDate);
	}
}