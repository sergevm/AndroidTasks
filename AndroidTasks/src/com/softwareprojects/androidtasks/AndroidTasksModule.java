package com.softwareprojects.androidtasks;

import roboguice.config.AbstractAndroidModule;
import roboguice.inject.SharedPreferencesName;

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
			
		bind(ILog.class).to(Logger.class).asEagerSingleton();
		bind(ReminderCalculations.class).to(ReminderCalculationFactory.class).asEagerSingleton();
		bind(RecurrenceCalculations.class).to(RecurrenceCalculationFactory.class).asEagerSingleton();
		
		// Task scheduling
		bind(TaskAlarmManager.class).to(AndroidTaskAlarmManager.class).asEagerSingleton();
				
		requestStaticInjection(TasksDBHelper.class);
		bind(TaskDateProvider.class).to(TaskDateProviderImpl.class).asEagerSingleton();
		bind(TaskRepository.class).to(SqliteTaskRepository.class).asEagerSingleton();
		requestStaticInjection(TaskScheduler.class);
		
		// Synchronization manager
		requestStaticInjection(SynchronizationManager.class);
	}
}
