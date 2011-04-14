package taskdatecalculationtests;


import java.util.Calendar;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import com.softwareprojects.androidtasks.domain.TaskDateCalculation;
import com.softwareprojects.androidtasks.domain.TaskDateProvider;
import com.softwareprojects.androidtasks.domain.dates.HoursCalculation;

public class Given_an_hourscalculation {

	private static final int SHIFT = 1;
	TaskDateCalculation hours;
	TaskDateProvider dates;
	Calendar expectationCalendar;
	Calendar taskDateProviderCalendar;
	
	@Before
	public void setUp() throws Exception {
		dates = mock(TaskDateProvider.class);
		
		expectationCalendar = Calendar.getInstance();
		taskDateProviderCalendar = (Calendar)expectationCalendar.clone();
		
		when(dates.getNow()).thenReturn(taskDateProviderCalendar);
		when(dates.getToday()).thenReturn(taskDateProviderCalendar);
		
		hours = new HoursCalculation();
	}

	@Test
	public void When_the_offset_date_is_in_the_future_then_offset_time_is_returned() {
		
		taskDateProviderCalendar.add(Calendar.DATE, -1);
		Date next = hours.getNext(expectationCalendar.getTime(), dates, SHIFT);

		expectationCalendar.add(Calendar.HOUR, SHIFT);
		
		assertEquals(expectationCalendar.getTime(), next);
	}
	
	@Test
	public void When_no_offset_date_Then_null_is_returned() {
		Date next = hours.getNext(null, dates, 1);
		assertNull(next);
	}
	
	@Test
	public void When_the_offset_date_is_in_the_past_Then_a_date_is_calculated() {
		taskDateProviderCalendar.add(Calendar.DATE, 10);
		
		Date next = hours.getNext(expectationCalendar.getTime(), dates, SHIFT);
		assertNotNull(next);
	}

	@Test
	public void When_the_offset_date_is_in_the_past_Then_the_correct_date_is_calculated() {
		taskDateProviderCalendar.add(Calendar.DATE, 10);
		
		Date next = hours.getNext(expectationCalendar.getTime(), dates, SHIFT);
		
		expectationCalendar.add(Calendar.HOUR, 1);
		expectationCalendar.add(Calendar.DATE, 10);
		
		Date expected = expectationCalendar.getTime();
		
		assertEquals(expected, next);
	}
}
