package tasktests.snoozing;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Calendar;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import tasktests.TaskTestBase;

import com.softwareprojects.androidtasks.domain.Task;
import com.softwareprojects.androidtasks.domain.TaskDateProvider;

public class Given_the_snooze_time_is_after_the_next_reminder extends TaskTestBase {
	
	private Calendar reminderDate;
	private TaskDateProvider dates;

	@Before
	public void setup(){
		
		super.setup();
		
		dates = mock(TaskDateProvider.class);		
		reminderDate = Calendar.getInstance();
				
		task.setReminderType(Task.REMINDER_HOURLY);
		task.setReminderDate(reminderDate.getTime());
	
		reminderDate.add(Calendar.MINUTE, -5);		
		when(dates.getNow()).thenReturn(reminderDate);
}
	
	@Test
	public void Then_the_snoozing_is_cancelled() {
		Date snoozedTime = task.snooze(dates, 10);
		assertNull(snoozedTime);
	}
	
	@Test
	public void Then_the_snooze_count_is_unaffected() {
		int before = task.getSnoozeCount();
		task.snooze(dates, 10);
		assertEquals(before, task.getSnoozeCount());
	}
}