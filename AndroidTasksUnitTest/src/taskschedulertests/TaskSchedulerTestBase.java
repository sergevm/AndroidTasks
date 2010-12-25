package taskschedulertests;

import static org.mockito.Mockito.mock;

import java.util.Calendar;
import java.util.Date;

import org.junit.Before;

import com.softwareprojects.androidtasks.domain.ILog;
import com.softwareprojects.androidtasks.domain.RecurrenceCalculations;
import com.softwareprojects.androidtasks.domain.ReminderCalculations;
import com.softwareprojects.androidtasks.domain.Task;
import com.softwareprojects.androidtasks.domain.TaskAlarmManager;
import com.softwareprojects.androidtasks.domain.TaskDateCalculation;
import com.softwareprojects.androidtasks.domain.TaskDateProvider;
import com.softwareprojects.androidtasks.domain.TaskRepository;
import com.softwareprojects.androidtasks.domain.TaskScheduler;


public abstract class TaskSchedulerTestBase {

	protected Task task;
	protected TaskAlarmManager alarms;
	protected ReminderCalculations reminders;
	protected RecurrenceCalculations recurrences;
	protected TaskDateProvider dates;
	protected TaskDateCalculation reminder;
	protected TaskScheduler taskScheduler;
	protected TaskRepository taskRepository;
	protected ILog logger;

	@Before
	public void setUp() throws Exception {

		logger = mock(ILog.class);
		task = mock(Task.class);
		reminder = mock(TaskDateCalculation.class);
		dates = mock(TaskDateProvider.class);
		alarms = mock(TaskAlarmManager.class);
		reminders = mock(ReminderCalculations.class);
		recurrences = mock(RecurrenceCalculations.class);
		taskRepository = mock(TaskRepository.class);

		taskScheduler = new TaskScheduler(reminders, recurrences, alarms, dates, taskRepository, logger);		
	}
	
	protected Date createFutureDate() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(2099, 1, 1);
		
		return calendar.getTime();
	}
}