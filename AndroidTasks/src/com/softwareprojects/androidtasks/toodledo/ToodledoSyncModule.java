package com.softwareprojects.androidtasks.toodledo;

import roboguice.config.AbstractAndroidModule;

import com.domaindriven.toodledo.RestClientFactory;
import com.softwareprojects.androidtasks.db.SqliteToodledoRepository;
import com.softwareprojects.androidtasks.db.ToodledoDBHelper;
import com.softwareprojects.androidtasks.domain.sync.TaskSynchronizer;

public class ToodledoSyncModule extends AbstractAndroidModule {

	@Override
	protected void configure() {

		requestStaticInjection(ToodledoDBHelper.class);
		
		bind(ToodledoRepository.class).to(SqliteToodledoRepository.class).asEagerSingleton();	
		bind(RestClientFactory.class).to(HttpRestClientFactory.class).asEagerSingleton();
		bind(TaskSynchronizer.class).to(ToodledoSynchronizer.class).asEagerSingleton();		
	
	}

}
