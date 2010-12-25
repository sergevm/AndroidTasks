package taskdatecalculationtests;


import java.util.Calendar;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import com.softwareprojects.androidtasks.domain.TaskDateProvider;
import com.softwareprojects.androidtasks.domain.dates.HoursCalculation;

public class Given_an_hourscalculation {

	HoursCalculation hours;
	TaskDateProvider dates;
	Calendar calendar;
	Calendar taskDateProviderCalendar;
	
	@Before
	public void setUp() throws Exception {
		dates = mock(TaskDateProvider.class);
		
		calendar = Calendar.getInstance();
		taskDateProviderCalendar = (Calendar)calendar.clone();
		
		when(dates.getNow()).thenReturn(taskDateProviderCalendar);
		when(dates.getToday()).thenReturn(taskDateProviderCalendar);
		
		hours = new HoursCalculation();
	}

	@Test
	public void When_the_offset_date_is_in_the_future_then_offset_time_is_returned() {
		taskDateProviderCalendar.add(Calendar.DATE, -1);
		
		Date next = hours.getNext(calendar.getTime(), dates, 1);
		assertEquals(calendar.getTime(), next);
	}
	
	@Test
	public void When_no_offset_date_Then_null_is_returned() {
		Date next = hours.getNext(null, dates, 1);
		assertNull(next);
	}
	
	@Test
	public void When_the_offset_date_is_in_the_past_Then_a_date_is_calculated() {
		taskDateProviderCalendar.add(Calendar.DATE, 10);
		
		Date next = hours.getNext(calendar.getTime(), dates, 1);
		assertNotNull(next);
	}

	@Test
	public void When_the_offset_date_is_in_the_past_Then_the_correct_date_is_calculated() {
		taskDateProviderCalendar.add(Calendar.DATE, 10);
		
		Date next = hours.getNext(calendar.getTime(), dates, 1);
		taskDateProviderCalendar.add(Calendar.HOUR, 1);
		Date expected = taskDateProviderCalendar.getTime();
		
		assertEquals(expected, next);
	}
}
