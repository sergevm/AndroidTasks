package taskschedulertests.purging;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Calendar;

import org.junit.Before;
import org.junit.Test;

import taskschedulertests.TaskSchedulerTestBase;

public class When_purging_without_a_positive_number_of_weeks extends TaskSchedulerTestBase {
	
	Calendar calendar;
	
	@Before public void setUp() throws Exception {
		super.setUp();

		calendar = Calendar.getInstance();
		calendar.set(2011, 12, 1, 14, 12);
		when(dates.getNow()).thenReturn(calendar);
	}
	
	@Test public void then_the_purging_notification_is_cancelled() {
		taskScheduler.purge(0);
		verify(alarms, times(1)).removePurgeAlarm();
	}
	
	@Test public void then_no_purge_is_performed_on_the_repository() {
		taskScheduler.purge(0);
		verify(taskRepository, never()).purge(anyInt());
	}
}
