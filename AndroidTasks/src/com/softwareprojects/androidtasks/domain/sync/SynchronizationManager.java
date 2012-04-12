package com.softwareprojects.androidtasks.domain.sync;

import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.domaindriven.toodledo.SyncException;
import com.google.inject.Inject;
import com.softwareprojects.androidtasks.domain.ILog;
import com.softwareprojects.androidtasks.domain.Task;
import com.softwareprojects.androidtasks.domain.TaskRepository;
import com.softwareprojects.androidtasks.domain.TaskScheduler;

public class SynchronizationManager {

	private final ILog log;
	private TaskScheduler scheduler;
	private TaskRepository repository;
	private final TaskSynchronizer synchronizer;

	private static final String TAG = SynchronizationManager.class.getSimpleName();

	@Inject
	public SynchronizationManager(TaskSynchronizer synchronizer, TaskScheduler scheduler, TaskRepository repository, ILog log) {
		this.synchronizer = synchronizer;
		this.repository = repository;
		this.scheduler = scheduler;
		this.log = log;
	}

	public SynchronizationResult sync() {

		log.v(TAG, "Synchronization starting");

		Calendar localSyncTime = Calendar.getInstance();
		SynchronizationResult result;
		
		result = processLocalAdds();
		if(result.getClass().isAssignableFrom(Failure.class)) {
			return result;
		}
		
		result = processLocalDeletes();
		if(result.getClass().isAssignableFrom(Failure.class)) {
			return result;
		}

		result = processRemoteUpdates();
		if(result.getClass().isAssignableFrom(Failure.class)) {
			return result;
		}

		result = processRemoteDeletes();
		if(result.getClass().isAssignableFrom(Failure.class)) {
			return result;
		}
		
		processLocalUpdates();
		if(result.getClass().isAssignableFrom(Failure.class)) {
			return result;
		}

		log.v(TAG, "Synchronization completed");

		try {
			synchronizer.updateSyncStatus(localSyncTime);
		} catch (SyncException e) {
			e.printStackTrace();
			return new Failure(e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			return new Failure(e.getMessage());
		}

		return new Success();
	}

	private SynchronizationResult processLocalUpdates() {
		SynchronizationResult updateResult = null;

		List<Task> tasksUpdatedLocally = repository.getUpdatedSince(synchronizer.getLastEditTime());

		log.d(TAG, String.format("Processing local updates: # of tasks: %d", tasksUpdatedLocally.size()));

		if(tasksUpdatedLocally.size() > 0) {
			updateResult = synchronizer.updateTasks(tasksUpdatedLocally);
		}
		else {
			return new NullSynchronizationResult("No local updates to sync");
		}

		log.d(TAG, updateResult.toString());

		return updateResult;
	}

	private SynchronizationResult processLocalDeletes() {
		SynchronizationResult deleteResult = null;

		List<Task> tasksDeletedLocally = repository.getDeletedSince(synchronizer.getLastDeleteTime());

		log.d(TAG, String.format("Processing local deletes: # of tasks: %d", tasksDeletedLocally.size()));

		if(tasksDeletedLocally.size() > 0) {
			deleteResult = synchronizer.deleteTasks(tasksDeletedLocally);
		}
		else {
			deleteResult = new NullSynchronizationResult("No local deletes to sync");
		}
		
		log.d(TAG, deleteResult.toString());

		return deleteResult;
	}

	private SynchronizationResult processLocalAdds() {
		SynchronizationResult addResult = null;

		List<Task> tasksAddedLocally = repository.getNewSince(synchronizer.getLastEditTime());

		log.d(TAG, String.format("Processing local adds: # of tasks: %d", tasksAddedLocally.size()));

		if(tasksAddedLocally.size() > 0) {
			addResult = synchronizer.addTasks(tasksAddedLocally);
		}
		else {
			addResult = new NullSynchronizationResult("No local adds to sync");
		}
		
		log.d(TAG, addResult.toString());

		return addResult;
	}

	private SynchronizationResult processRemoteUpdates() {

		Map<String, Task> remoteUpdatedTasks;
		try {
			remoteUpdatedTasks = synchronizer.getUpdated();
		} catch (SyncException e) {
			e.printStackTrace();
			return new Failure(e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			return new Failure(e.getMessage());
		}
		
		Map<String,Task> unregisteredTasks = new HashMap<String,Task>();

		if(remoteUpdatedTasks == null || remoteUpdatedTasks.size() == 0) {
			return new NullSynchronizationResult("No updated remote tasks found");
		}

		for(String remoteKey : remoteUpdatedTasks.keySet()) {
			Task task = remoteUpdatedTasks.get(remoteKey);
			boolean isNew = task.getId() == 0;
			scheduler.schedule(task);
			
			if(isNew) unregisteredTasks.put(remoteKey, task);
		}
		
		try {
			synchronizer.register(unregisteredTasks);
		} catch (ParseException e) {
			log.e(TAG, "An exception occurred while registering a Toodledo mapping");
			return new Failure("An exception occurred trying to parse the modification date");
		}
		
		return new Success("Remotely updated tasks synced successfully");
	}

	private SynchronizationResult processRemoteDeletes() {

		List<Long> remoteDeletedTasks;
		try {
			remoteDeletedTasks = synchronizer.getDeleted();
		} catch (SyncException e) {
			e.printStackTrace();
			return new Failure(e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			return new Failure(e.getMessage());
		}

		if(remoteDeletedTasks == null || remoteDeletedTasks.size() == 0)
			return new NullSynchronizationResult("No deleted remote tasks found");

		for(long id : remoteDeletedTasks) {

			Task task = repository.find(id);

			if(task != null) {
				scheduler.delete(task);
			}
		}		
		
		synchronizer.unregister(remoteDeletedTasks);
		
		return new Success("Remotely deleted tasks synced successfully");
	}
}
