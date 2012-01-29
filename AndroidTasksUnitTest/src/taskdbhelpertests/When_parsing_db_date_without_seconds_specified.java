package taskdbhelpertests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import com.softwareprojects.androidtasks.db.DbDateFormatter;
import com.softwareprojects.androidtasks.domain.ILog;

public class When_parsing_db_date_without_seconds_specified {

	final static String dateAsString = "2011-01-01 10:00"; 
	ILog log = mock(ILog.class);
	
	@Before
	public void prepare() {
		DbDateFormatter.log = log;
	}
	
	@Test
	public void then_a_correct_date_is_parsed() throws ParseException {
		
		Date date = DbDateFormatter.parse(dateAsString);
		
		assertNotNull(date);
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		
		assertEquals(10, cal.get(Calendar.HOUR_OF_DAY));
		assertEquals(0, cal.get(Calendar.MINUTE));
	}
	
	@Test
	public void then_a_warning_is_logged_about_wrong_database_format() throws ParseException {
		
		DbDateFormatter.parse(dateAsString);

		verify(log).w(anyString(), eq(String.format("%s is a legacy date representation in the Android database", dateAsString)));
	}
}
