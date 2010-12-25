package taskschedulertests.snoozing;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.*;

import java.util.Calendar;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import taskschedulertests.TaskSchedulerTestBase;

import com.softwareprojects.androidtasks.domain.NotificationSource;
import com.softwareprojects.androidtasks.domain.Task;

public class Given_the_task_returns_a_snoozed_time extends TaskSchedulerTestBase {

	Date snoozedTime;
	
	@Before public void setUp() throws Exception {
		super.setUp();
		
		Calendar now = Calendar.getInstance();
		now.set(2011, 1, 1);
		now.add(Calendar.MINUTE, 10);
		snoozedTime = now.getTime();

		when(task.snooze(dates, 10)).thenReturn(snoozedTime);
	}
	
	@Test public void Then_the_alarmmanager_should_snooze_to_the_snoozed_time() {

		taskScheduler.snooze(task, 10, NotificationSource.ALARMSOURCE_TARGETDATE);		
		verify(alarms, times(1)).snoozeAlarm(task, snoozedTime, NotificationSource.ALARMSOURCE_SNOOZE_TARGETDATE);
	}
	
	@Test public void When_the_notification_source_is_target_date_Then_the_alarm_is_set_for_target_date() {
		taskScheduler.snooze(task, 10, NotificationSource.ALARMSOURCE_TARGETDATE);		
		verify(alarms, times(1)).snoozeAlarm(any(Task.class), any(Date.class), eq(NotificationSource.ALARMSOURCE_SNOOZE_TARGETDATE));
	}

	@Test public void When_the_notification_source_is_reminder_date_Then_the_alarm_is_set_for_target_date() {
		taskScheduler.snooze(task, 10, NotificationSource.ALARMSOURCE_REMINDERDATE);		
		verify(alarms, times(1)).snoozeAlarm(any(Task.class), any(Date.class), eq(NotificationSource.ALARMSOURCE_SNOOZE_REMINDERDATE));
	}

	@Test public void When_the_notification_source_is_snooze_target_date_Then_the_alarm_is_set_for_target_date() {
		taskScheduler.snooze(task, 10, NotificationSource.ALARMSOURCE_SNOOZE_TARGETDATE);		
		verify(alarms, times(1)).snoozeAlarm(any(Task.class), any(Date.class), eq(NotificationSource.ALARMSOURCE_SNOOZE_TARGETDATE));
	}

	@Test public void When_the_notification_source_is_snooze_reminder_date_Then_the_alarm_is_set_for_target_date() {
		taskScheduler.snooze(task, 10, NotificationSource.ALARMSOURCE_SNOOZE_REMINDERDATE);		
		verify(alarms, times(1)).snoozeAlarm(any(Task.class), any(Date.class), eq(NotificationSource.ALARMSOURCE_SNOOZE_REMINDERDATE));
	}
}

