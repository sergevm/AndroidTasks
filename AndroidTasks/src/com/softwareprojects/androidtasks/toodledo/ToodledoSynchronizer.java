package com.softwareprojects.androidtasks.toodledo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.content.SharedPreferences;
import android.util.Log;

import com.domaindriven.toodledo.Account;
import com.domaindriven.toodledo.Session;
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

		session = Session.create(user, password);
		account = Account.create(session);
	}

	@Override
	public SynchronizationResult addTasks(List<Task> tasks) {
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