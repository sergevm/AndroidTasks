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

import com.softwareprojects.androidtasks.domain.TaskDateCalculation;
import com.softwareprojects.androidtasks.domain.TaskDateProvider;
import com.softwareprojects.androidtasks.domain.dates.DaysCalculation;
import com.softwareprojects.androidtasks.domain.dates.MonthsCalculation;

public class Given_a_monthscalculation {

	TaskDateCalculation months;
	TaskDateProvider dates;
	Calendar expectationCalendar;
	Calendar taskDateProviderCalendar;
	
	@Before
	public void setUp() throws Exception {
		dates = mock(TaskDateProvider.class);
		
		expectationCalendar = Calendar.getInstance();
		taskDateProviderCalendar = (Calendar)expectationCalendar.clone();
		
		when(dates.getNow()).thenReturn(taskDateProviderCalendar);
		
		months = new MonthsCalculation();
	}

	@Test
	public void When_the_offset_date_is_in_the_future_then_offset_time_is_returned() {
		taskDateProviderCalendar.add(Calendar.DATE, -1);
		
		Date next = months.getNext(expectationCalendar.getTime(), dates, 1);
		assertEquals(expectationCalendar.getTime(), next);
	}
	
	@Test
	public void When_no_offset_date_Then_null_is_returned() {
		Date next = months.getNext(null, dates, 1);
		assertNull(next);
	}
	
	@Test
	public void When_the_offset_date_is_in_the_past_Then_a_date_is_calculated() {
		taskDateProviderCalendar.add(Calendar.MONTH, 2);
		
		Date next = months.getNext(expectationCalendar.getTime(), dates, 1);
		assertNotNull(next);
	}

	@Test
	public void When_the_offset_date_is_in_the_past_Then_the_correct_date_is_calculated() {
		// Let's set today to 2 months and 3 days from now
		taskDateProviderCalendar.add(Calendar.MONTH, 2);
		taskDateProviderCalendar.add(Calendar.DATE, 3);
		
		Date next = months.getNext(expectationCalendar.getTime(), dates, 1);
		
		// Then the calculated date should be 3 months from the offset date
		expectationCalendar.add(Calendar.MONTH, 3);
		
		Date expected = expectationCalendar.getTime();
		
		assertEquals(expected, next);
	}
}
