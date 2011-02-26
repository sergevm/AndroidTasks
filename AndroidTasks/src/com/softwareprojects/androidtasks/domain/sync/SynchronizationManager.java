package com.softwareprojects.androidtasks.domain.sync;

import java.util.Calendar;
import java.util.List;

import org.json.JSONException;

import com.softwareprojects.androidtasks.domain.ILog;
import com.softwareprojects.androidtasks.domain.Task;
import com.softwareprojects.androidtasks.domain.TaskRepository;
import com.softwareprojects.androidtasks.domain.TaskScheduler;

public class SynchronizationManager {

	private final ILog log;
	private TaskScheduler scheduler;
	private TaskRepository repository;
	private final Synchronizer synchronizer;

	private static final String TAG = SynchronizationManager.class.getSimpleName();

	public SynchronizationManager(Synchronizer synchronizer, TaskScheduler scheduler, TaskRepository repository, ILog log) {
		this.synchronizer = synchronizer;
		this.repository = repository;
		this.scheduler = scheduler;
		this.log = log;
	}

	public SynchronizationResult sync() {

		log.v(TAG, "Synchronization starting");

		try {
			Calendar localSyncTime = Calendar.getInstance();

			processLocalAdds();
			processLocalDeletes();
			processRemoteUpdates();
			processRemoteDeletes();
			processLocalUpdates();
			
			synchronizer.updateSyncStatus(localSyncTime);

		} catch (JSONException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		log.v(TAG, "Synchronization completed");

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
			return new NoSync("No local updates to sync");
		}

		log.d(TAG, updateResult.toString());

		return updateResult;
	}

	private SynchronizationResult processLocalDeletes() throws JSONException, Exception {
		SynchronizationResult deleteResult = null;

		List<Task> tasksDeletedLocally = repository.getDeletedSince(synchronizer.getLastDeleteTime());

		log.d(TAG, String.format("Processing local deletes: # of tasks: %d", tasksDeletedLocally.size()));

		if(tasksDeletedLocally.size() > 0) {
			deleteResult = synchronizer.deleteTasks(tasksDeletedLocally);
		}
		else {
			deleteResult = new NoSync("No local deletes to sync");
		}
		
		log.d(TAG, deleteResult.toString());

		return deleteResult;
	}

	private SynchronizationResult processLocalAdds() throws JSONException, Exception {
		SynchronizationResult addResult = null;

		List<Task> tasksAddedLocally = repository.getNewSince(synchronizer.getLastEditTime());

		log.d(TAG, String.format("Processing local adds: # of tasks: %d", tasksAddedLocally.size()));

		if(tasksAddedLocally.size() > 0) {
			addResult = synchronizer.addTasks(tasksAddedLocally);
		}
		else {
			addResult = new NoSync("No local adds to sync");
		}
		
		log.d(TAG, addResult.toString());

		return addResult;
	}

	private void processRemoteUpdates() throws JSONException, Exception {

		try {
			List<Task> remoteUpdatedTasks = synchronizer.getUpdated();

			if(remoteUpdatedTasks == null || remoteUpdatedTasks.size() == 0)
				return;

			for(Task task : remoteUpdatedTasks) {
				scheduler.schedule(task);
			}
		}
		catch(JSONException ex) {
			log.d(TAG, ex.getMessage());
		}
	}

	private void processRemoteDeletes() throws JSONException, Exception {

		try {
			List<Long> remoteDeletedTasks = synchronizer.getDeleted();

			if(remoteDeletedTasks == null || remoteDeletedTasks.size() == 0)
				return;

			for(long id : remoteDeletedTasks) {

				Task task = repository.find(id);

				if(task != null) {
					scheduler.delete(task);
				}
			}		
		}
		catch(JSONException ex) {
			log.d(TAG, ex.getMessage());
		}

	}
}
