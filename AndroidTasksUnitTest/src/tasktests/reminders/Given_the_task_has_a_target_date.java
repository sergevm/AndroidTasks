package tasktests.reminders;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Calendar;

import org.junit.Before;
import org.junit.Test;

import tasktests.TaskTestBase;

import com.softwareprojects.androidtasks.domain.ReminderCalculations;
import com.softwareprojects.androidtasks.domain.Task;
import com.softwareprojects.androidtasks.domain.TaskDateCalculation;
import com.softwareprojects.androidtasks.domain.TaskDateProvider;


public class Given_the_task_has_a_target_date extends TaskTestBase {

	Calendar calendar;
	private ReminderCalculations reminders;
	private TaskDateProvider dates;
	private TaskDateCalculation calculation;
	
	@Before
	public void setup() {
		super.setup();
		
		dates = mock(TaskDateProvider.class);
		reminders = mock(ReminderCalculations.class);
		calculation = mock(TaskDateCalculation.class);
		
		when(reminders.create(task)).thenReturn(calculation);
		
		calendar = Calendar.getInstance();
		task.setTargetDate(calendar.getTime());
				
		task.setReminderType(Task.REMINDER_HOURLY);
		task.setReminderDate(calendar.getTime());
	}
	
	@Test
	public void When_updating_reminders_Then_a_calculation_is_requested() {
		task.updateReminder(reminders, dates);
		verify(reminders, times(1)).create(task);
	}
	
	@Test
	public void When_updating_reminders_Then_a_reminder_time_is_calculated() {
		task.updateReminder(reminders, dates);
		verify(calculation, times(1)).getNext(calendar.getTime(), dates, 1);
	}
	
	@Test
	public void When_the_target_date_is_in_the_past_Then_the_reminder_date_is_set_to_first_future_reminder_date() {
		
		Calendar now = (Calendar) calendar.clone();
		now.add(Calendar.DATE, 20);
		
		when(dates.getNow()).thenReturn(now);
		when(calculation.getNext(task.getTargetDate(), dates, 1)).thenReturn(now.getTime());
		
		task.initializeReminders(reminders, dates);
	
		assertEquals(now.getTime(), task.getReminderDate());
	}
}
