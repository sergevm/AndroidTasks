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

public class Given_the_snooze_time_is_before_the_next_reminder extends TaskTestBase {
	
	private TaskDateProvider dates;
	private Calendar reminderDate;

	@Before
	public void setup(){
		
		super.setup();
		
		dates = mock(TaskDateProvider.class);		
		reminderDate = Calendar.getInstance();
				
		task.setReminderType(Task.REMINDER_HOURLY);
		task.setReminderDate(reminderDate.getTime());
	
		reminderDate.add(Calendar.MINUTE, -15);		
		when(dates.getNow()).thenReturn((Calendar)reminderDate.clone());
	}
	
	@Test
	public void Then_a_snooze_time_is_calculated() {
		Date snoozedTime = task.snooze(dates, 10);
		assertNotNull(snoozedTime);
	}
	
	@Test
	public void Then_the_calculated_snoozed_time_is_correct() {
		Date snoozedTime = task.snooze(dates, 10);
		reminderDate.add(Calendar.MINUTE, 10);
		assertEquals(reminderDate.getTime(), snoozedTime);
	}
	
	@Test
	public void Then_the_snooze_count_is_incremented() {
		int before = task.getSnoozeCount();
		task.snooze(dates, 10);
		assertEquals(before + 1, task.getSnoozeCount());
	}
}