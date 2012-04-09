package com.softwareprojects.androidtasks.toodledo;

import java.util.Calendar;

import roboguice.service.RoboService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.domaindriven.androidtools.Connectivity;
import com.google.inject.Inject;
import com.softwareprojects.androidtasks.Constants;
import com.softwareprojects.androidtasks.domain.TaskAlarmManager;
import com.softwareprojects.androidtasks.domain.sync.Failure;
import com.softwareprojects.androidtasks.domain.sync.Success;
import com.softwareprojects.androidtasks.domain.sync.SynchronizationManager;
import com.softwareprojects.androidtasks.domain.sync.SynchronizationResult;
import com.softwareprojects.androidtasks.domain.sync.TaskSynchronizer;

public class ToodledoSyncService extends RoboService {

	@Inject	SynchronizationManager manager;
	@Inject	SharedPreferences preferences;
	@Inject	TaskSynchronizer synchronizer;
	@Inject	TaskAlarmManager alarms;

	static String TAG = ToodledoSyncService.class.getSimpleName();

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

		if (!Connectivity.isNetworkAvailable(getApplication())) {
			Log.i(TAG, "No network connectivity available. Sync is cancelled");
			return 0;
		}

		new SychronizeTask().execute();
		return super.onStartCommand(intent, flags, startId);

	}

	private class SychronizeTask extends AsyncTask<Object, Integer, Long> {

		@Override
		protected Long doInBackground(Object... params) {
			try {
				
				String user = preferences.getString(Constants.PREFS_TOODLEDO_USER, null);
				String pwd = preferences.getString(Constants.PREFS_TOODLEDO_PWD, null);
				String appId = preferences.getString(Constants.PREFS_TOODLEDO_APP_ID, null);
				String appToken = preferences.getString(Constants.PREFS_TOODLEDO_APP_TOKEN, null);

				synchronizer.init(user, pwd, appId, appToken);
			
			} catch (Exception ex) {
				Log.e(TAG, ex.getMessage(), ex);
				return 1L;
			}

			SynchronizationResult result = manager.sync();
			Log(result);
			return 0L;
		}

		private void Log(SynchronizationResult result) {
			if (result.getClass() == Success.class) {
				Log.i(TAG, "Success");
			} else if (result.getClass() == Failure.class) {
				Log.e(TAG, result.toString());
			}
		}

		@Override
		protected void onPostExecute(Long result) {

			Intent intent = new Intent(
					"com.softwareprojects.androidtasks.TASKLISTCHANGE");
			sendBroadcast(intent);

			Calendar nextSync = Calendar.getInstance();
			nextSync.add(Calendar.HOUR, 1);

			alarms.setSynchronizationAlarm(nextSync);

			if (result == 0L) {
				Toast toast = Toast.makeText(getApplicationContext(), "Synchronization completed.", Toast.LENGTH_SHORT);
				toast.show();
			} else if (result == 1L) {
				Toast toast = Toast.makeText(getApplicationContext(), "Synchronization failed. Please check the log.", Toast.LENGTH_SHORT);
				toast.show();
			}
			super.onPostExecute(result);
		}
	}
}
