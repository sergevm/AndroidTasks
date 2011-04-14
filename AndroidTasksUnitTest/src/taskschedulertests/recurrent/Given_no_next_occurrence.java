package taskschedulertests.recurrent;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Calendar;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import taskschedulertests.TaskSchedulerTestBase;

import com.softwareprojects.androidtasks.domain.Task;

public class Given_no_next_occurrence extends TaskSchedulerTestBase {

	Calendar datesCalendar = Calendar.getInstance();
	Calendar calendar = (Calendar)datesCalendar.clone();
	
	@Before
	public void setUp() throws Exception {
		super.setUp();
				
		when(task.isRecurrent()).thenReturn(true);
		when(task.createNextInstance(recurrences, dates)).thenReturn(null);
	}

	@Test public void When_targetdate_in_future_then_instantiation_is_scheduled() {
		
		when(dates.getNow()).thenReturn(datesCalendar);

		calendar.add(Calendar.DATE, 1);
		
		when(task.hasTargetDateInFuture(dates)).thenReturn(true);
		when(task.getTargetDate()).thenReturn(calendar.getTime());
		when(task.getReminderDate()).thenReturn(calendar.getTime());

		taskScheduler.scheduleRecurrentTaskInstantiationFor(task);
		
		verify(alarms, times(1)).setInstantiateRecurrentTaskAlarm(task, calendar.getTime());
	}

	@Test public void When_targetdate_in_past_Then_task_instantiation_is_not_scheduled() {

		when(dates.getNow()).thenReturn(datesCalendar);

		when(task.hasTargetDateInFuture(dates)).thenReturn(false);

		calendar.add(Calendar.DATE, 2);
		when(task.getReminderDate()).thenReturn(calendar.getTime());

		taskScheduler.scheduleRecurrentTaskInstantiationFor(task);
		
		verify(alarms, times(0)).setInstantiateRecurrentTaskAlarm(any(Task.class), any(Date.class));
	}

	@Test public void then_no_alarms_should_be_set() {
		
		taskScheduler.scheduleRecurrentTaskInstantiationFor(task);
		
		verify(alarms, never()).setInstantiateRecurrentTaskAlarm(any(Task.class), any(Date.class));
		verify(alarms, never()).setNextReminderNotificationAlarm(any(Task.class));
		verify(alarms, never()).setNextReminderNotificationAlarm(any(Task.class));
	}
		
	@Test public void then_no_task_should_be_persisted() {
		
		taskScheduler.scheduleRecurrentTaskInstantiationFor(task);
		
		verify(taskRepository, never()).insert(any(Task.class));
		verify(taskRepository, never()).update(any(Task.class));
	}
}
