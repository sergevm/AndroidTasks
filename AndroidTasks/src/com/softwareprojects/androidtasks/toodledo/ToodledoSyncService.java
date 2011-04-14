package com.softwareprojects.androidtasks.toodledo;

import java.util.Calendar;

import roboguice.service.RoboService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.IBinder;

import com.google.inject.Inject;
import com.softwareprojects.androidtasks.domain.TaskAlarmManager;
import com.softwareprojects.androidtasks.domain.sync.SynchronizationManager;
import com.softwareprojects.androidtasks.domain.sync.TaskSynchronizer;

public class ToodledoSyncService extends RoboService {

	@Inject SynchronizationManager manager;
	@Inject SharedPreferences preferences;
	@Inject TaskSynchronizer synchronizer;
	@Inject TaskAlarmManager alarms;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		new SychronizeTask().execute();
		return super.onStartCommand(intent, flags, startId);
		
	}
	
	private class SychronizeTask extends AsyncTask<Object, Integer,Long> {

		@Override
		protected Long doInBackground(Object... params) {
			synchronizer.init("td4d35ff02625cc", "HitTheRoadJack!");
			manager.sync();
			return 0L;
		}

		@Override
		protected void onPostExecute(Long result) {
			
			Intent intent = new Intent("com.softwareprojects.androidtasks.TASKLISTCHANGE");		
			sendBroadcast(intent);
			
			Calendar nextSync = Calendar.getInstance();
			nextSync.add(Calendar.HOUR, 1);
			alarms.setSynchronizationAlarm(nextSync);

			super.onPostExecute(result);
		}		
	}
}
