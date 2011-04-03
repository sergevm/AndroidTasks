package com.softwareprojects.androidtasks;

import roboguice.config.AbstractAndroidModule;
import roboguice.inject.SharedPreferencesName;

import com.google.inject.Singleton;
import com.softwareprojects.androidtasks.db.SqliteTaskRepository;
import com.softwareprojects.androidtasks.db.TasksDBHelper;
import com.softwareprojects.androidtasks.domain.ILog;
import com.softwareprojects.androidtasks.domain.Logger;
import com.softwareprojects.androidtasks.domain.RecurrenceCalculationFactory;
import com.softwareprojects.androidtasks.domain.RecurrenceCalculations;
import com.softwareprojects.androidtasks.domain.ReminderCalculationFactory;
import com.softwareprojects.androidtasks.domain.ReminderCalculations;
import com.softwareprojects.androidtasks.domain.TaskAlarmManager;
import com.softwareprojects.androidtasks.domain.TaskDateProvider;
import com.softwareprojects.androidtasks.domain.TaskDateProviderImpl;
import com.softwareprojects.androidtasks.domain.TaskRepository;
import com.softwareprojects.androidtasks.domain.TaskScheduler;
import com.softwareprojects.androidtasks.domain.sync.SynchronizationManager;

public class AndroidTasksModule extends AbstractAndroidModule {

	@Override
	protected void configure() {

		// Name of the shared preferences file
		bindConstant().annotatedWith(SharedPreferencesName.class).to("androidtasks");
			
		bind(ILog.class).to(Logger.class);
		bind(ReminderCalculations.class).to(ReminderCalculationFactory.class).in(Singleton.class);
		bind(RecurrenceCalculations.class).to(RecurrenceCalculationFactory.class).in(Singleton.class);
		
		// Task scheduling
		bind(TaskAlarmManager.class).to(AndroidTaskAlarmManager.class).in(Singleton.class);
				
		bind(TasksDBHelper.class).in(Singleton.class);
		bind(TaskDateProvider.class).to(TaskDateProviderImpl.class).in(Singleton.class);
		bind(TaskRepository.class).to(SqliteTaskRepository.class).in(Singleton.class);
		bind(TaskScheduler.class).in(Singleton.class);
		
		// Synchronization manager
		bind(SynchronizationManager.class).in(Singleton.class);
	}
}
