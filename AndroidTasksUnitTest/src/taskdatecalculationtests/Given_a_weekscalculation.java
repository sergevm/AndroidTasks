package taskdatecalculationtests;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Calendar;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import com.softwareprojects.androidtasks.domain.TaskDateProvider;
import com.softwareprojects.androidtasks.domain.dates.WeeksCalculation;

public class Given_a_weekscalculation {

	private static final int SHIFT = 5;
	WeeksCalculation weeks;
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
		
		weeks = new WeeksCalculation();
	}

	@Test
	public void When_the_offset_date_is_in_the_future_then_offset_time_is_incremented_with_shift() {
		
		taskDateProviderCalendar.add(Calendar.DATE, -1);
		Date next = weeks.getNext(expectationCalendar.getTime(), dates, SHIFT);

		expectationCalendar.add(Calendar.WEEK_OF_YEAR, SHIFT);
		
		assertEquals(expectationCalendar.getTime(), next);
	}
	
	@Test
	public void When_no_offset_date_Then_null_is_returned() {
		Date next = weeks.getNext(null, dates, SHIFT);
		assertNull(next);
	}
	
	@Test
	public void When_the_offset_date_is_in_the_past_Then_a_date_is_calculated() {
		taskDateProviderCalendar.add(Calendar.DATE, 2);
		
		Date next = weeks.getNext(expectationCalendar.getTime(), dates, SHIFT);
		assertNotNull(next);
	}

	@Test
	public void When_the_offset_date_is_in_the_past_Then_the_correct_date_is_calculated() {
		taskDateProviderCalendar.add(Calendar.DATE, 2);
		
		Date next = weeks.getNext(expectationCalendar.getTime(), dates, SHIFT);
		expectationCalendar.add(Calendar.WEEK_OF_MONTH, SHIFT);
		Date expected = expectationCalendar.getTime();
		
		assertEquals(expected, next);
	}
}
