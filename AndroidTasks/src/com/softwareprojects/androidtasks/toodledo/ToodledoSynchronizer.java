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
import com.domaindriven.toodledo.GetDeletedTasksRequest;
import com.domaindriven.toodledo.GetDeletedTasksResponse;
import com.domaindriven.toodledo.GetUpdatedTasksRequest;
import com.domaindriven.toodledo.GetUpdatedTasksResponse;
import com.domaindriven.toodledo.RestClientFactory;
import com.domaindriven.toodledo.Session;
import com.domaindriven.toodledo.ToodledoSession;
import com.domaindriven.toodledo.UpdateTasksRequest;
import com.domaindriven.toodledo.UpdateTasksResponse;
import com.softwareprojects.androidtasks.domain.Task;
import com.softwareprojects.androidtasks.domain.sync.Failure;
import com.softwareprojects.androidtasks.domain.sync.NoSync;
import com.softwareprojects.androidtasks.domain.sync.Success;
import com.softwareprojects.androidtasks.domain.sync.SynchronizationResult;
import com.softwareprojects.androidtasks.domain.sync.Synchronizer;

public class ToodledoSynchronizer implements Synchronizer {

	private static final String TAG = ToodledoSynchronizer.class.getSimpleName();

	private Account account;
	private Session session;
	private final ToodledoRepository repository;
	private final ToodledoSyncState synchronizationState;

	private final RestClientFactory factory;

	public ToodledoSynchronizer(SharedPreferences preferences, ToodledoRepository repository, RestClientFactory factory) {
		this.factory = factory;
		this.repository = repository;
		this.synchronizationState = new ToodledoSyncState(preferences);
	}

	public void init(final String user, final String password) {

		session = ToodledoSession.create(user, password, new ToodledoSession.Log(){
			@Override public void log(String tag, String message) {
				Log.d(tag, message);
			}},factory);

		account = Account.create(session, factory);
	}

	@Override
	public SynchronizationResult addTasks(List<Task> localTasks) throws JSONException, Exception {

		Log.v(TAG, "addTasks");

		if(localTasks == null || localTasks.size() == 0) {
			return new NoSync("The list of locally added tasks is empty");
		}
		
		try {
			List<com.domaindriven.toodledo.Task> remoteTasks = new ArrayList<com.domaindriven.toodledo.Task>();

			for(Task task : localTasks){
				
				if(repository.findRemoteIdByLocalId(task.getId()) != null) {
					continue;
				}
				
				com.domaindriven.toodledo.Task toodledoTask = new com.domaindriven.toodledo.Task();

				toodledoTask.setTitle(task.getDescription());
				remoteTasks.add(toodledoTask);
			}
			
			if(remoteTasks.size() == 0) {
				return new NoSync("No locally added tasks to sync with Toodledo");
			}
			
			AddTasksRequest request = new AddTasksRequest(session, remoteTasks, factory);
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
			return new NoSync("The list of locally deleted tasks is empty");
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
				return new NoSync("No locally deleted tasks to sync with Toodledo");

			DeleteTasksRequest request = new DeleteTasksRequest(session, remoteIds, factory);
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


		if(tasks == null || tasks.size() == 0) {
			return new NoSync("The list of locally updated tasks is empty");
		}

		try {
			List<com.domaindriven.toodledo.Task> toodledoTasks = new ArrayList<com.domaindriven.toodledo.Task>();

			for(Task task : tasks) {

				String remoteId = repository.findRemoteIdByLocalId(task.getId());

				if(remoteId != null) {
					
					com.domaindriven.toodledo.Task toodledoTask = new com.domaindriven.toodledo.Task();
					toodledoTask.setId(remoteId);
					toodledoTask.setTitle(task.getDescription());
					toodledoTask.setCompleted(task.isCompleted());
					toodledoTask.setModified(task.getModificationDate().getTime() / 1000);
					
					toodledoTasks.add(toodledoTask);
				}
			}

			if(toodledoTasks.size() == 0)
				return new NoSync("No locally updated tasks to sync with Toodledo");

			UpdateTasksRequest request = new UpdateTasksRequest(session, toodledoTasks, factory);
			UpdateTasksResponse response = new UpdateTasksResponse(session, request);

			toodledoTasks = response.parse();

			return new Success();
		} 
		catch(JSONException e) {
			
			e.printStackTrace();
			return new Failure(e.getMessage());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return new Success();
	}

	@Override
	public List<Task> getUpdated() throws JSONException, Exception {

		Log.v(TAG, "getUpdated");

		List<Task> mappedTasks = null;

		if(account.getLastEditTask() > synchronizationState.getLastEditTimestamp()) {

			GetUpdatedTasksRequest request = new GetUpdatedTasksRequest(session, synchronizationState.getLastEditTimestamp(), factory);
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

			GetDeletedTasksRequest request = new GetDeletedTasksRequest(session, synchronizationState.getLastDeleteTimestamp(), factory);
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

		account = Account.create(session, factory);
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