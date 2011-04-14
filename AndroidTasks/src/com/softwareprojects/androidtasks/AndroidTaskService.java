package com.softwareprojects.androidtasks;

import roboguice.service.RoboService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

import com.google.inject.Inject;
import com.softwareprojects.androidtasks.domain.Task;
import com.softwareprojects.androidtasks.domain.TaskRepository;
import com.softwareprojects.androidtasks.domain.TaskScheduler;

public class AndroidTaskService extends RoboService {

	static final String TAG = AndroidTaskService.class.getSimpleName();

	@Inject
	TaskRepository repository;
	@Inject
	TaskScheduler scheduler;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.i(TAG, "onCreate");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		Log.i(TAG, "onStartCommand");

		if (intent.getDataString().startsWith(Constants.ANDROIDTASK_TASK_NEXT_RECURRENCE_URI)) {

			long taskId = intent.getLongExtra(Constants.ALARM_TASK_ID, 0);
			Log.v(TAG, "New recurrent task instance is requested for task with id " + taskId);

			Task task = repository.find(taskId);
			scheduler.instantiateNextOccurrenceOf(task);

			broadcastTaskListChange();
		}

		else if (intent.getDataString().equals(Constants.ANDROIDTASK_TASK_PURGE)) {

			SharedPreferences preferences = getSharedPreferences("AndroidTasks", Context.MODE_PRIVATE);
			int weeks = preferences.getInt(Constants.PREFS_PURGING_TASK_AGE_IN_WEEKS, -1);

			scheduler.purge(weeks);

			broadcastTaskListChange();
		}

		return super.onStartCommand(intent, flags, startId);
	}

	private void broadcastTaskListChange() {
		Intent intent = new Intent("com.softwareprojects.androidtasks.TASKLISTCHANGE");
		sendBroadcast(intent);
	}
}
