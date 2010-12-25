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


public class Given_the_task_has_no_target_date extends TaskTestBase{

	Calendar calendar;
	TaskDateProvider dates;
	TaskDateCalculation calculation;
	ReminderCalculations reminders;

	@Before
	public void setup() {
		super.setup();
		
		dates = mock(TaskDateProvider.class);
		reminders = mock(ReminderCalculations.class);
		calculation = mock(TaskDateCalculation.class);
		
		when(reminders.create(task)).thenReturn(calculation);
		
		calendar = Calendar.getInstance();
		task.setReminderType(Task.REMINDER_WEEKLY);
		task.setReminderDate(calendar.getTime());
	}
	
	@Test
	public void When_updating_reminders_Then_the_reminder_date_is_used_to_perform_the_update() {
		task.updateReminder(reminders, dates);
		verify(calculation, times(1)).getNext(calendar.getTime(), dates, 1);
	}
	
	@Test
	public void When_updating_reminders_Then_the_a_calculation_is_requested() {
		task.updateReminder(reminders, dates);
		verify(reminders, times(1)).create(task);
	}
}
