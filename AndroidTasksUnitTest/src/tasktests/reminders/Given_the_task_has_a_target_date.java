package tasktests.reminders;

import java.util.Calendar;

import org.junit.Before;
import org.junit.Test;

import com.softwareprojects.androidtasks.domain.ReminderCalculations;
import com.softwareprojects.androidtasks.domain.Task;
import com.softwareprojects.androidtasks.domain.TaskDateCalculation;
import com.softwareprojects.androidtasks.domain.TaskDateProvider;

import tasktests.TaskTestBase;
import static org.mockito.Mockito.*;


public class Given_the_task_has_a_target_date extends TaskTestBase {

	Calendar calendar;
	private ReminderCalculations reminders;
	private TaskDateProvider dates;
	private TaskDateCalculation calculation;
	
	@Before
	public void setup() {
		super.setup();
		
		reminders = mock(ReminderCalculations.class);
		dates = mock(TaskDateProvider.class);
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
}
