package taskschedulertests.recurrent;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import taskschedulertests.TaskSchedulerTestBase;

import com.softwareprojects.androidtasks.domain.Task;

public class Given_no_next_occurrence extends TaskSchedulerTestBase {

	@Before
	public void setUp() throws Exception {
		super.setUp();
		when(task.createNextOccurrence(recurrences, dates)).thenReturn(null);
	}

	@Test public void then_the_task_should_be_requested_for_a_next_occurrence() {
		taskScheduler.initializeNextOccurrence(task);
		
		verify(task, times(1)).createNextOccurrence(recurrences, dates);
	}

	@Test public void then_no_alarms_should_be_set() {
		taskScheduler.initializeNextOccurrence(task);
		verify(alarms, never()).setRecurrent(any(Task.class), any(Date.class));
		verify(alarms, never()).setReminder(any(Task.class));
		verify(alarms, never()).setReminder(any(Task.class));
	}
		
	@Test public void then_no_task_should_be_persisted() {
		taskScheduler.initializeNextOccurrence(task);
		
		verify(taskRepository, never()).insert(any(Task.class));
		verify(taskRepository, never()).update(any(Task.class));
	}
}
