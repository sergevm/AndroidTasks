package tasktests;

import org.junit.Before;

import com.softwareprojects.androidtasks.domain.Task;


public class TaskTestBase {

	protected Task task;

	@Before
	public void setup() {
		task = new Task();
	}

}
