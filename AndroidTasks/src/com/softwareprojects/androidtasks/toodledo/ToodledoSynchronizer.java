package com.softwareprojects.androidtasks.toodledo;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.domaindriven.toodledo.Response;
import com.domaindriven.toodledo.RestClientFactory;
import com.domaindriven.toodledo.Session;
import com.domaindriven.toodledo.SyncException;
import com.domaindriven.toodledo.ToodledoSession;
import com.domaindriven.toodledo.ToodledoTimestamp;
import com.domaindriven.toodledo.UpdateTasksRequest;
import com.domaindriven.toodledo.UpdateTasksResponse;
import com.google.inject.Inject;
import com.softwareprojects.androidtasks.domain.Task;
import com.softwareprojects.androidtasks.domain.sync.Failure;
import com.softwareprojects.androidtasks.domain.sync.NullSynchronizationResult;
import com.softwareprojects.androidtasks.domain.sync.Success;
import com.softwareprojects.androidtasks.domain.sync.SynchronizationResult;
import com.softwareprojects.androidtasks.domain.sync.TaskSynchronizer;

public class ToodledoSynchronizer implements TaskSynchronizer {

	private static final String TAG = ToodledoSynchronizer.class.getSimpleName();

	private Account account;
	private Session session;
	private final RestClientFactory factory;
	private final ToodledoRepository repository;
	private final ToodledoSyncTime synchronizationState;

	@Inject
	public ToodledoSynchronizer(SharedPreferences preferences, ToodledoRepository repository, RestClientFactory factory) throws ParseException {
		this.factory = factory;
		this.repository = repository;
		this.synchronizationState = new ToodledoSyncTime(preferences);
	}

	@Override
	public void init(final String user, final String password, final String appId, final String appToken) throws IOException, SyncException {

		session = ToodledoSession.create(user, password, appId, appToken, new ToodledoSession.Log(){
			@Override public void log(String tag, String message) {
				Log.d(tag, message);
			}},factory);

		account = Account.create(session, factory);
	}

	@Override
	public SynchronizationResult addTasks(List<Task> localTasks) {

		Log.v(TAG, "addTasks");

		if(localTasks == null || localTasks.size() == 0) {
			return new NullSynchronizationResult("The list of locally added tasks is empty");
		}
		
		try {
			List<com.domaindriven.toodledo.Task> remoteTasks = new ArrayList<com.domaindriven.toodledo.Task>();

			for(Task task : localTasks){
				
				if(repository.findRemoteIdByLocalId(task.getId()) != null) {
					continue;
				}
				
				com.domaindriven.toodledo.Task toodledoTask = new com.domaindriven.toodledo.Task();

				toodledoTask.setTitle(task.getDescription());
				toodledoTask.setNote(task.getNotes());
				
				if(task.getTargetDate() != null) {
					toodledoTask.setDueDate(ToodledoTimestamp.GetGMTTimeInSeconds(task.getTargetDate()));
				}
				
				remoteTasks.add(toodledoTask);
			}
			
			if(remoteTasks.size() == 0) {
				return new NullSynchronizationResult("No locally added tasks to sync with Toodledo");
			}
			
			AddTasksRequest request = new AddTasksRequest(session, remoteTasks, factory);
			Response<List<com.domaindriven.toodledo.Task>> response = new AddTasksResponse(session, request);

			remoteTasks = response.parse();

			int index = 0;

			for(com.domaindriven.toodledo.Task task : remoteTasks) {
				repository.deleteByLocalId(localTasks.get(index).getId());
				repository.insert(localTasks.get(index++).getId(), task.getId(), task.getModified());
			}
			
			return new Success();
		} 
		catch(IOException ex) {
			Log.e(TAG, ex.getMessage());
			return new Failure(ex.getMessage());
		}
		catch(SyncException e) {
			Log.e(TAG, e.getMessage());
			return new Failure(e.getMessage());
		}
		catch(ParseException e) {
			Log.e(TAG, e.getMessage());
			return new Failure(e.getMessage());
		}
	}

	@Override
	public SynchronizationResult deleteTasks(List<Task> tasks) {

		Log.v(TAG, "deleteTasks");

		if(tasks == null || tasks.size() == 0) {
			return new NullSynchronizationResult("The list of locally deleted tasks is empty");
		}

		try {
			List<String> remoteIds = new ArrayList<String>();

			for(Task task : tasks) {

				String remoteId = repository.findRemoteIdByLocalId(task.getId());

				if(remoteId != null) {
					remoteIds.add(String.valueOf(remoteId));
				}
			}

			if(remoteIds.size() == 0) {
				return new NullSynchronizationResult("No locally deleted tasks to sync with Toodledo");
			}
			
			DeleteTasksRequest request = new DeleteTasksRequest(session, remoteIds, factory);
			Response<List<String>> response = new DeleteTasksResponse(session, request);

			remoteIds = response.parse();

			for(String remoteId : remoteIds) {
				repository.deleteByRemoteId(remoteId);
			}

			return new Success();
		} 
		catch(SyncException ex) {
			Log.e(TAG, ex.getMessage());
			return new Failure(ex.getMessage());
		}
		catch(IOException ex) {
			Log.e(TAG, ex.getMessage());
			return new Failure(ex.getMessage());
		}
	}

	@Override
	public SynchronizationResult updateTasks(List<Task> tasks) {
		Log.v(TAG, "updateTasks");


		if(tasks == null || tasks.size() == 0) {
			return new NullSynchronizationResult("The list of locally updated tasks is empty");
		}

		try {
			List<com.domaindriven.toodledo.Task> toodledoTasks = new ArrayList<com.domaindriven.toodledo.Task>();

			for(Task task : tasks) {

				String remoteId = repository.findRemoteIdByLocalId(task.getId());

				if(remoteId != null) {
					
					long completedInGMTSeconds = ToodledoTimestamp.GetGMTTimeInSeconds(task.getModificationDate());

					com.domaindriven.toodledo.Task toodledoTask = new com.domaindriven.toodledo.Task();
					toodledoTask.setId(remoteId);
					toodledoTask.setTitle(task.getDescription());
					toodledoTask.setModified(completedInGMTSeconds);
					toodledoTask.setNote(task.getNotes());

					if(task.isCompleted()) {
						toodledoTask.setCompleted(completedInGMTSeconds);
					}
										
					if(task.getTargetDate() != null) {
						toodledoTask.setDueDate(ToodledoTimestamp.GetGMTTimeInSeconds(task.getTargetDate()));
					}
					
					toodledoTasks.add(toodledoTask);
				}
			}

			if(toodledoTasks.size() == 0)
				return new NullSynchronizationResult("No locally updated tasks to sync with Toodledo");

			UpdateTasksRequest request = new UpdateTasksRequest(session, toodledoTasks, factory);
			UpdateTasksResponse response = new UpdateTasksResponse(session, request);

			toodledoTasks = response.parse();

			return new Success();
		} 
		catch(IOException e) {
			Log.e(TAG, e.getMessage());
			return new Failure(e.getMessage());
		}
		catch(SyncException e) {
			Log.e(TAG, e.getMessage());
			return new Failure(e.getMessage());
		}
		catch(ParseException e) {
			Log.e(TAG, e.getMessage());
			return new Failure(e.getMessage());
		}
	}

	@Override
	public Map<String,Task> getUpdated() throws SyncException, IOException {

		Log.v(TAG, "getUpdated");

		Map<String,Task> mappedTasks = null;
		

		if(account.getLastEditTask() > synchronizationState.getLastEditTimestamp()) {

			GetUpdatedTasksRequest request = new GetUpdatedTasksRequest(session, synchronizationState.getLastEditTimestamp(), factory);
			GetUpdatedTasksResponse response = new GetUpdatedTasksResponse(session, request);

			List<com.domaindriven.toodledo.Task> updatedTasks = response.parse();

			mappedTasks = translateUpdated(updatedTasks);
		}

		return mappedTasks;
	}

	@Override
	public List<Long> getDeleted() throws SyncException, IOException {

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
	public void register(Map<String,Task> tasks) throws ParseException {
		
		for(String remoteId : tasks.keySet()) {
			
			Task task = tasks.get(remoteId);
			
			removeExistingLocalIdMapping(task.getId());
			removeExistingRemoteIdMapping(remoteId);
			insertRemoteIdMapping(remoteId, task);
		}
	}

	@Override
	public void unregister(List<Long> tasks) {
		
		for(Long id : tasks) {
			repository.deleteByLocalId(id);
		}
	}

	@Override
	public void updateSyncStatus(Calendar localSyncTime) throws IOException, SyncException {

		account = Account.create(session, factory);
		synchronizationState.setLastSyncTime(localSyncTime);
		synchronizationState.setLastEditTimestamp(account.getLastEditTask());
		synchronizationState.setLastDeleteTimestamp(account.getLastDeleteTask());

		synchronizationState.save();
	}

	private void insertRemoteIdMapping(final String remoteId, final Task task) throws ParseException {
		repository.insert(task.getId(), remoteId, 
				ToodledoTimestamp.GetGMTTimeInSeconds(task.getModificationDate()));
	}

	private void removeExistingLocalIdMapping(long id) {
		if(id > 0) {
			repository.deleteByLocalId(id);
		}
	}

	private void removeExistingRemoteIdMapping(String remoteId) {
		
		Long localId = repository.findLocalIdByRemoteId(remoteId);
		
		if(localId > 0) {
			Log.w(TAG, String.format("Deleting local id '%i' mapping from Toodle sync mapping", localId));
			repository.deleteByLocalId(localId);
		}
	}

	private Map<String,Task> translateUpdated(List<com.domaindriven.toodledo.Task> updatedTasks) {

		Map<String,Task> tasks = new HashMap<String,Task>();

		for(com.domaindriven.toodledo.Task updatedTask : updatedTasks) {

			long localId = repository.findLocalIdByRemoteId(updatedTask.getId());

			Task task = new Task();
			task.setId(localId);
			task.setDescription(updatedTask.getTitle());
			task.setNotes(updatedTask.getNote());
			
			long dueDate = updatedTask.getDueDate();
			if(dueDate > 0) {
				try {
					task.setTargetDate(ToodledoTimestamp.GetLocalDateTime(dueDate));
				} catch (ParseException e) {
					Log.e(TAG, "Parsing exception on due date of task");
				}			
			}
			
			if(updatedTask.getCompleted() > 0) {
				task.complete();
			}

			tasks.put(updatedTask.getId(),task);
		}

		return tasks;
	}
}