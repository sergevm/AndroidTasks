package com.softwareprojects.androidtasks.domain.sync;

import java.util.List;
import java.util.ListIterator;

import org.json.JSONException;

import com.softwareprojects.androidtasks.domain.ILog;
import com.softwareprojects.androidtasks.domain.Task;
import com.softwareprojects.androidtasks.domain.TaskRepository;

public class SynchronizationManager {

	private final Synchronizer synchronizer;
	private final TaskRepository localTasks;
	private final ILog log;
	
	private static final String TAG = SynchronizationManager.class.getSimpleName();

	public SynchronizationManager(Synchronizer synchronizer, TaskRepository localTasks, ILog log) {
		this.synchronizer = synchronizer;
		this.localTasks = localTasks;
		this.log = log;
	}

	public SynchronizationResult sync() {

		log.v(TAG, "Synchronization starting");
		
		try {
			processLocalAdds();
			processLocalDeletes();
			processRemoteUpdates();
			processLocalUpdates();
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		log.v(TAG, "Synchronization completed");

		return new SynchronizationSuccess();
	}

	private SynchronizationResult processLocalUpdates() {
		List<Task> tasksUpdatedLocally = localTasks.getUpdatedSince(synchronizer.getLastSyncTime());
		
		log.d(TAG, String.format("Processing local updates: # of tasks: %d", tasksUpdatedLocally.size()));
		
		SynchronizationResult updateResult = synchronizer.updateTasks(tasksUpdatedLocally);
		return updateResult;
	}

	private SynchronizationResult processLocalDeletes() {
		List<Task> tasksDeletedLocally = localTasks.getDeletedSince(synchronizer.getLastSyncTime());

		log.d(TAG, String.format("Processing local deletes: # of tasks: %d", tasksDeletedLocally.size()));

		SynchronizationResult deleteResult = synchronizer.deleteTasks(tasksDeletedLocally);
		return deleteResult;
	}

	private SynchronizationResult processLocalAdds() throws JSONException, Exception {
		List<Task> tasksAddedLocally = localTasks.getNewSince(synchronizer.getLastSyncTime());

		log.d(TAG, String.format("Processing local adds: # of tasks: %d", tasksAddedLocally.size()));

		SynchronizationResult addResult = synchronizer.addTasks(tasksAddedLocally);
		return addResult;
	}

	private void processRemoteUpdates() {
			
		List<Task> tasksAddedRemotely = synchronizer.getNew();
		ListIterator<Task> iterator = tasksAddedRemotely.listIterator();

		if(iterator.hasNext() == true) {
			do {
				Task task = iterator.next();
				localTasks.insert(task);
			}
			while(iterator.hasNext());
		}
	}
}
