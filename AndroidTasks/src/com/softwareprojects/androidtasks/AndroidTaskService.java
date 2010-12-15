package com.softwareprojects.androidtasks;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.softwareprojects.androidtasks.db.DBHelper;
import com.softwareprojects.androidtasks.db.SqliteTaskRepository;
import com.softwareprojects.androidtasks.domain.RecurrenceCalculationFactory;
import com.softwareprojects.androidtasks.domain.RecurrenceCalculations;
import com.softwareprojects.androidtasks.domain.ReminderCalculationFactory;
import com.softwareprojects.androidtasks.domain.ReminderCalculations;
import com.softwareprojects.androidtasks.domain.Task;
import com.softwareprojects.androidtasks.domain.TaskAlarmManager;
import com.softwareprojects.androidtasks.domain.TaskDateProvider;
import com.softwareprojects.androidtasks.domain.TaskDateProviderImpl;
import com.softwareprojects.androidtasks.domain.TaskRepository;
import com.softwareprojects.androidtasks.domain.TaskScheduler;

public class AndroidTaskService extends Service {

	static final String TAG = AndroidTaskService.class.getSimpleName();
	DBHelper dbHelper;
	TaskDateProvider dates;
	TaskRepository repository;
	TaskScheduler scheduler;
	TaskAlarmManager alarmManager;
	ReminderCalculations reminders;
	RecurrenceCalculations recurrences;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();

		dbHelper = new DBHelper(this);
		dates = new TaskDateProviderImpl();
		reminders = new ReminderCalculationFactory();
		recurrences = new RecurrenceCalculationFactory();
		repository = new SqliteTaskRepository(dbHelper);
		alarmManager = new AndroidTaskAlarmManager(this);
		
		
		scheduler = new TaskScheduler(reminders, recurrences, alarmManager, dates, repository);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		if(intent.getDataString().startsWith(Constants.ANDROIDTASK_TASK_NEXT_RECURRENCE_URI)) {
			
			long taskId = intent.getLongExtra(Constants.ALARM_TASK_ID, 0);
			Log.v(TAG, "New recurrent task instance is requested for task with id " + taskId);
			
			Task task = dbHelper.getSingle(taskId);
			scheduler.createNextOccurrence(task);
			
			broadcastTaskListChange();
		}
		
		return super.onStartCommand(intent, flags, startId);
	}
	
	private void broadcastTaskListChange() {
		Intent intent = new Intent("com.softwareprojects.androidtasks.TASKLISTCHANGE");		
		sendBroadcast(intent);
	}
}
