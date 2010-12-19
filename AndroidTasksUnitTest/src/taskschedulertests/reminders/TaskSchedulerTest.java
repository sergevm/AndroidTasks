package taskschedulertests.reminders;

import java.util.Calendar;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import taskschedulertests.TaskSchedulerTestBase;

import com.softwareprojects.androidtasks.domain.NotificationSource;
import com.softwareprojects.androidtasks.domain.RecurrenceCalculations;
import com.softwareprojects.androidtasks.domain.TaskDateCalculation;
import com.softwareprojects.androidtasks.domain.ReminderCalculations;
import com.softwareprojects.androidtasks.domain.Task;
import com.softwareprojects.androidtasks.domain.TaskAlarmManager;
import com.softwareprojects.androidtasks.domain.TaskDateProvider;
import com.softwareprojects.androidtasks.domain.TaskRepository;
import com.softwareprojects.androidtasks.domain.TaskScheduler;

import static org.mockito.Mockito.*;

import junit.framework.TestCase;

public class TaskSchedulerTest extends TaskSchedulerTestBase {

	Calendar now = Calendar.getInstance();

	@Before
	public void setUp() throws Exception {
		super.setUp();
		
		when(reminders.create(task)).thenReturn(reminder);
		when(dates.getNow()).thenReturn(now);
}

	@Test
	public void WITH_a_reminder_set_and_no_targetdate_set_THEN_reminderdate_unchanged() {
	
		Calendar calendar = Calendar.getInstance();	
		calendar.add(Calendar.MINUTE, 20);
		task.setReminderDate(calendar.getTime());
		
		taskScheduler.snooze(task, 10, NotificationSource.ALARMSOURCE_REMINDERDATE);
		verify(task, times(1)).setReminderDate(calendar.getTime());
	}

	@Test
	public void WITH_a_reminder_set_and_no_targetdate_set_THEN_alarmmanager_is_called() {
		
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MINUTE, 20);		
		task.setReminderDate(calendar.getTime());
		
		taskScheduler.snooze(task, 10, NotificationSource.ALARMSOURCE_REMINDERDATE);
		
		verify(alarms, times(1)).snoozeAlarm(task, now.getTime(), 10, NotificationSource.ALARMSOURCE_SNOOZE_REMINDERDATE);
	}

	@Test
	public void WITH_a_targetdate_set_WHEN_reminder_type_manual_THEN_alarmmanager_is_called() {
		
		Calendar calendar = Calendar.getInstance();
		task.setTargetDate(calendar.getTime());
		task.initializeReminders(reminders, dates);
		
		taskScheduler.snooze(task, 10, NotificationSource.ALARMSOURCE_TARGETDATE);
		
		verify(alarms, times(1)).snoozeAlarm(task, now.getTime(), 10, NotificationSource.ALARMSOURCE_SNOOZE_TARGETDATE);
	}

	@Test
	public void WITH_a_targetdate_set_WHEN_reminder_type_manual_THEN_targetdate_unchanged() {
		
		Calendar calendar = Calendar.getInstance();
		task.setTargetDate(calendar.getTime());
		task.initializeReminders(reminders, dates);
		
		taskScheduler.snooze(task, 10, NotificationSource.ALARMSOURCE_TARGETDATE);
		
		verify(task, times(1)).setTargetDate(calendar.getTime());
	}
}

