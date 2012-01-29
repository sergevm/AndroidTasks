package taskdbhelpertests;

import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.text.ParseException;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;

import com.softwareprojects.androidtasks.db.DbDateFormatter;
import com.softwareprojects.androidtasks.domain.ILog;

public class When_parsing_db_date_that_makes_no_sense {

	static final String dateAsString = "whateva";
	@InjectMocks ILog log;
	
	@Before
	public void before() {
		
		log = mock(ILog.class);
		DbDateFormatter.log = log;
	}
	
	@Test
	public void then_an_error_is_logged() throws ParseException {
		
		DbDateFormatter.parse(dateAsString);
		verify(log).e(anyString(), eq(String.format("%s is not a valid date representation in the AndroidTasks database", dateAsString)));
	}
	
	@Test
	public void then_a_null_date_is_returned() throws ParseException {
		
		Date returned = DbDateFormatter.parse(dateAsString);
		assertNull(returned);
	}
}
