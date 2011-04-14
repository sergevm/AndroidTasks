package taskdatecalculationtests;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Calendar;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import com.softwareprojects.androidtasks.domain.TaskDateProvider;
import com.softwareprojects.androidtasks.domain.dates.MinutesCalculation;

public class Given_a_minutescalculation {

	private static final int SHIFT = 38;
	MinutesCalculation minutes;
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
		
		minutes = new MinutesCalculation();
	}

	@Test
	public void When_the_offset_date_is_in_the_future_then_the_offset_time_is_incremented() {
		
		taskDateProviderCalendar.add(Calendar.MINUTE, -1);
		Date next = minutes.getNext(expectationCalendar.getTime(), dates, SHIFT);
		
		expectationCalendar.add(Calendar.MINUTE, SHIFT);
		
		Date expected = expectationCalendar.getTime();
		assertEquals(expected, next);
	}
	
	@Test
	public void When_no_offset_date_then_null_is_returned() {
		Date next = minutes.getNext(null, dates, SHIFT);
		assertNull(next);
	}
	
	@Test
	public void When_the_offset_date_is_in_the_past_then_a_date_is_calculated() {
		taskDateProviderCalendar.add(Calendar.DATE, 2);
		
		Date next = minutes.getNext(expectationCalendar.getTime(), dates, SHIFT);
		assertNotNull(next);
	}
	
	@Test
	public void When_the_offset_date_is_in_the_past_then_the_calculated_date_is_not_the_offset_date() {
		
		taskDateProviderCalendar.add(Calendar.MINUTE, 1);
		Date next = minutes.getNext(expectationCalendar.getTime(), dates, SHIFT);

		Date expected = expectationCalendar.getTime();
		assertFalse(expected.equals(next));
	}

	@Test
	public void When_the_offset_date_is_in_the_past_then_the_correct_date_is_calculated() {
		
		taskDateProviderCalendar.add(Calendar.MINUTE, 1);
		Date next = minutes.getNext(expectationCalendar.getTime(), dates, SHIFT);
		
		expectationCalendar.add(Calendar.MINUTE, SHIFT);

		Date expected = expectationCalendar.getTime();
		assertEquals(expected, next);
	}
}
