package com.softwareprojects.androidtasks.toodledo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.json.JSONException;

import android.content.SharedPreferences;
import android.util.Log;

import com.domaindriven.toodledo.Account;
import com.domaindriven.toodledo.AddTasksRequest;
import com.domaindriven.toodledo.AddTasksResponse;
import com.domaindriven.toodledo.Session;
import com.domaindriven.toodledo.ToodledoSession;
import com.softwareprojects.androidtasks.domain.Task;
import com.softwareprojects.androidtasks.domain.sync.SynchronizationResult;
import com.softwareprojects.androidtasks.domain.sync.SynchronizationSuccess;
import com.softwareprojects.androidtasks.domain.sync.Synchronizer;

public class ToodledoSynchronizer implements Synchronizer {

	private static final String TAG = ToodledoSynchronizer.class.getSimpleName();

	private Account account;
	private Session session;
	private final ToodledoSyncState synchronizationState;

	public ToodledoSynchronizer(SharedPreferences preferences) {
		this.synchronizationState = new ToodledoSyncState(preferences);
	}

	public void init(final String user, final String password) {

		session = ToodledoSession.create(user, password, new ToodledoSession.Log(){
					@Override public void log(String tag, String message) {
						Log.d(tag, message);
					}});

		account = Account.create(session);
	}

	@Override
	public SynchronizationResult addTasks(List<Task> tasks) throws JSONException, Exception {

		int index = 0;

		com.domaindriven.toodledo.Task toodledoTasks[] = new com.domaindriven.toodledo.Task[tasks.size()];

		for(Task task:tasks){
			com.domaindriven.toodledo.Task toodledoTask = new com.domaindriven.toodledo.Task();
			toodledoTask.setTitle(task.getDescription());

			toodledoTasks[index++] = toodledoTask;
		}

		AddTasksRequest request = new AddTasksRequest(session, toodledoTasks);
		AddTasksResponse response = new AddTasksResponse(session, request);

		toodledoTasks = response.parse();



		return new SynchronizationSuccess();
	}

	@Override
	public SynchronizationResult deleteTasks(List<Task> tasks) {
		return new SynchronizationSuccess();	
	}

	@Override
	public SynchronizationResult updateTasks(List<Task> tasks) {
		return new SynchronizationSuccess();
	}

	@Override
	public List<Task> getNew() {

		Log.v(TAG, "getNew");

		List<Task> newTasks = new ArrayList<Task>();

		if(account.getLastEditTask() > synchronizationState.getLastEditTimestamp()) {

		}

		return newTasks;
	}

	@Override
	public List<Task> getDeleted() {

		Log.v(TAG, "getDeleted");

		return new ArrayList<Task>();
	}

	@Override
	public List<Task> getUpdated() {

		Log.v(TAG, "getUpdated");

		return new ArrayList<Task>();
	}

	@Override
	public Calendar getLastSyncTime() {
		return synchronizationState.getLastSyncTime();
	}
}