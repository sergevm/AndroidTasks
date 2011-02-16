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
import com.domaindriven.toodledo.DeleteTasksRequest;
import com.domaindriven.toodledo.DeleteTasksResponse;
import com.domaindriven.toodledo.GetUpdatedTasksRequest;
import com.domaindriven.toodledo.GetUpdatedTasksResponse;
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

	private final ToodledoRepository repository;

	public ToodledoSynchronizer(SharedPreferences preferences, ToodledoRepository repository) {
		this.repository = repository;
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

		com.domaindriven.toodledo.Task[] addedToodledoTasks = response.parse();
		
		index = 0;
		
		for(com.domaindriven.toodledo.Task task : addedToodledoTasks) {
			repository.insert(tasks.get(index++).getId(), task.getId(), task.getModified());
		}

		return new SynchronizationSuccess();
	}

	@Override
	public SynchronizationResult deleteTasks(List<Task> tasks) throws JSONException, Exception {
		
		if(tasks == null || tasks.size() == 0) {
			return new SynchronizationSuccess();
		}
		
		List<String> toodledoTasksList = new ArrayList<String>();
		
		for(Task task : tasks) {
			
			String remoteId = repository.find(task.getId());
			
			// If no remote id is known, then the task was never logged in Toodledo before the 
			// task was deleted ...
			if(remoteId != null) {
				toodledoTasksList.add(String.valueOf(task.getId()));
			}
		}
		
		// If nothing to synchronize, don't ...
		if(toodledoTasksList.size() == 0)
			return new SynchronizationSuccess();
		
		// TODO: change signature to accept a List<String> ?
		String[] toDelete = new String[toodledoTasksList.size()];
		
		// ... but for now we still need an array ...
		int index = 0;
		for(String id : toodledoTasksList) {
			toDelete[index++] = id;
		}
		
		DeleteTasksRequest request = new DeleteTasksRequest(session, toDelete);
		DeleteTasksResponse response = new DeleteTasksResponse(session, request);
		
		toDelete = response.parse();
		
		return new SynchronizationSuccess();	
	}

	@Override
	public SynchronizationResult updateTasks(List<Task> tasks) {
		return new SynchronizationSuccess();
	}

	@Override
	public List<Task> getNew() throws JSONException, Exception {

		Log.v(TAG, "getNew");

		List<Task> newTasks = new ArrayList<Task>();

		if(account.getLastEditTask() > synchronizationState.getLastEditTimestamp()) {
			GetUpdatedTasksRequest request = new GetUpdatedTasksRequest(session, synchronizationState.getLastEditTimestamp());
			GetUpdatedTasksResponse response = new GetUpdatedTasksResponse(session, request);
			
			com.domaindriven.toodledo.Task[] updatedTasks = response.parse();
		}
		
		// TODO: Check if tasks exist, if they do, update them. Add them to the newTasks list ...

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