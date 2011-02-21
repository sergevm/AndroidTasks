package com.softwareprojects.androidtasks.toodledo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.json.JSONException;

import android.util.Log;
import android.content.SharedPreferences;

import com.domaindriven.toodledo.*;
import com.softwareprojects.androidtasks.domain.Task;
import com.softwareprojects.androidtasks.domain.sync.Failure;
import com.softwareprojects.androidtasks.domain.sync.NoSync;
import com.softwareprojects.androidtasks.domain.sync.Synchronizer;
import com.softwareprojects.androidtasks.domain.sync.SynchronizationResult;
import com.softwareprojects.androidtasks.domain.sync.Success;

public class ToodledoSynchronizer implements Synchronizer {

	private static final String TAG = ToodledoSynchronizer.class.getSimpleName();

	private Account account;
	private Session session;
	private final ToodledoRepository repository;
	private final ToodledoSyncState synchronizationState;

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
	public SynchronizationResult addTasks(List<Task> localTasks) throws JSONException, Exception {

		Log.v(TAG, "addTasks");

		try {
			List<com.domaindriven.toodledo.Task> remoteTasks = new ArrayList<com.domaindriven.toodledo.Task>();

			for(Task task : localTasks){
				com.domaindriven.toodledo.Task toodledoTask = new com.domaindriven.toodledo.Task();

				toodledoTask.setTitle(task.getDescription());
				remoteTasks.add(toodledoTask);
			}

			AddTasksRequest request = new AddTasksRequest(session, remoteTasks);
			AddTasksResponse response = new AddTasksResponse(session, request);

			remoteTasks = response.parse();

			int index = 0;

			for(com.domaindriven.toodledo.Task task : remoteTasks) {
				repository.insert(localTasks.get(index++).getId(), task.getId(), task.getModified());
			}
		} 
		catch(JSONException ex) {
			return new Failure(ex.getMessage());
		}

		return new Success();
	}

	@Override
	public SynchronizationResult deleteTasks(List<Task> tasks) throws JSONException, Exception {

		Log.v(TAG, "deleteTasks");

		if(tasks == null || tasks.size() == 0) {
			return new NoSync();
		}

		try {
			List<String> remoteIds = new ArrayList<String>();

			for(Task task : tasks) {

				String remoteId = repository.findRemoteIdByLocalId(task.getId());

				if(remoteId != null) {
					remoteIds.add(String.valueOf(remoteId));
				}
			}

			if(remoteIds.size() == 0)
				return new Success();

			DeleteTasksRequest request = new DeleteTasksRequest(session, remoteIds);
			DeleteTasksResponse response = new DeleteTasksResponse(session, request);

			remoteIds = response.parse();

			for(String remoteId : remoteIds) {
				repository.deleteByRemoteId(remoteId);
			}

			return new Success();
		} 
		catch(JSONException ex) {
			return new Failure(ex.getMessage());
		}
	}

	@Override
	public SynchronizationResult updateTasks(List<Task> tasks) {
		Log.v(TAG, "updateTasks");
		return new Success();
	}

	@Override
	public List<Task> getUpdated() throws JSONException, Exception {

		Log.v(TAG, "getUpdated");

		List<Task> mappedTasks = null;

		if(account.getLastEditTask() > synchronizationState.getLastEditTimestamp()) {

			GetUpdatedTasksRequest request = new GetUpdatedTasksRequest(session, synchronizationState.getLastEditTimestamp());
			GetUpdatedTasksResponse response = new GetUpdatedTasksResponse(session, request);

			List<com.domaindriven.toodledo.Task> updatedTasks = response.parse();

			mappedTasks = translateUpdated(updatedTasks);
		}

		return mappedTasks;
	}

	@Override
	public List<Long> getDeleted() throws JSONException, Exception {

		Log.v(TAG, "getDeleted");

		List<Long> localIds = new ArrayList<Long>();

		if(account.getLastDeleteTask() > synchronizationState.getLastDeleteTimestamp()) {

			GetDeletedTasksRequest request = new GetDeletedTasksRequest(session, synchronizationState.getLastDeleteTimestamp());
			GetDeletedTasksResponse response = new GetDeletedTasksResponse(session, request);

			List<String> toodledoIds = response.parse();

			if(toodledoIds == null || toodledoIds.size() == 0) { 
				return localIds;
			}

			for(String toodledoId : toodledoIds) {

				long localId = repository.findLocalIdByRemoteId(toodledoId);

				if(localId > 0) {
					localIds.add(localId);
				}
			}
		}

		return localIds;
	}

	@Override
	public Calendar getLastEditTime() {
		return synchronizationState.getLastEditTime();
	}

	@Override
	public Calendar getLastDeleteTime() {
		return synchronizationState.getLastDeleteTime();
	}

	@Override
	public void updateSyncStatus(Calendar localSyncTime) {

		account = Account.create(session);
		synchronizationState.setLastSyncTime(localSyncTime);
		synchronizationState.setLastEditTimestamp(account.getLastEditTask());
		synchronizationState.setLastDeleteTimestamp(account.getLastDeleteTask());

		synchronizationState.save();
	}

	private List<Task> translateUpdated(List<com.domaindriven.toodledo.Task> updatedTasks) {

		List<Task> tasks = new ArrayList<Task>();

		for(com.domaindriven.toodledo.Task updatedTask : updatedTasks) {

			long localId = repository.findLocalIdByRemoteId(updatedTask.getId());

			Task task = new Task();
			task.setId(localId);
			task.setDescription(updatedTask.getTitle());

			tasks.add(task);
		}

		return tasks;
	}
}