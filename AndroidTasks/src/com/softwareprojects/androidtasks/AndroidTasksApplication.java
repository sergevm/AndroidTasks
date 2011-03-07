package com.softwareprojects.androidtasks;

import roboguice.application.RoboApplication;
import android.content.Intent;

import com.softwareprojects.androidtasks.toodledo.ToodledoSyncModule;
import com.softwareprojects.androidtasks.toodledo.ToodledoSyncService;

public class AndroidTasksApplication extends RoboApplication {

	@Override
	protected void addApplicationModules(java.util.List<com.google.inject.Module> modules) {
		
		modules.add(new AndroidTasksModule());
		modules.add(new ToodledoSyncModule());
		
	}
	
	@Override
	public void onCreate() {

		super.onCreate();

		Intent serviceIntent = new Intent(this, ToodledoSyncService.class);
		this.startService(serviceIntent);
}
	
	@Override
	public void onTerminate() {
		
		Intent serviceIntent = new Intent(this, ToodledoSyncService.class);
		this.stopService(serviceIntent);

		super.onTerminate();
	}
}
