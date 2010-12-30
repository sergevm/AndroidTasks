package taskschedulertests.purging;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Calendar;

import org.junit.Before;
import org.junit.Test;

import taskschedulertests.TaskSchedulerTestBase;

public class When_purging_with_a_positive_number_of_weeks extends TaskSchedulerTestBase {

	Calendar calendar;
	
	@Before public void setUp() throws Exception {
		super.setUp();

		calendar = Calendar.getInstance();
		calendar.set(2011, 12, 1, 14, 12);
		when(dates.getNow()).thenReturn(calendar);
	}
	
	@Test public void then_the_repository_is_purged() {
		taskScheduler.purge(10);
		verify(taskRepository, times(1)).purge(10);
	}
	
	@Test public void then_the_notification_for_next_purge_is_set_up_for_next_day() {
		
		Calendar verificationCalendar = (Calendar)calendar.clone();
		verificationCalendar.add(Calendar.DATE, 1);
		
		taskScheduler.purge(10);
		verify(alarms, times(1)).schedulePurge(verificationCalendar);
	}
}
