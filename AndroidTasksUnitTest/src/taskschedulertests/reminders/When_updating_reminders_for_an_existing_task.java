package taskschedulertests.reminders;

import org.junit.Test;
import static org.mockito.Mockito.*;

import taskschedulertests.TaskSchedulerTestBase;


public class When_updating_reminders_for_an_existing_task extends TaskSchedulerTestBase {
	
	@Test public void Then_the_task_should_requested_to_update_its_reminder_date() {
		
		taskScheduler.updateReminder(task);
		verify(task, atLeastOnce()).updateReminder(reminders, dates);
	}
	
	@Test public void Then_the_reminder_alarms_should_be_updated() {
		
		when(task.getReminderDate()).thenReturn(createFutureDate());
		
		taskScheduler.updateReminder(task);
		verify(alarms, atLeastOnce()).setReminder(task);
	}
	
	@Test public void Then_the_repository_should_update_the_task() {

		taskScheduler.updateReminder(task);
		verify(taskRepository, atLeastOnce()).update(task);
	}
}
