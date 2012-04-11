package com.softwareprojects.androidtasks.toodledo;

import java.io.IOException;
import java.util.Calendar;

import roboguice.service.RoboService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.domaindriven.androidtools.AsyncTaskResult;
import com.domaindriven.androidtools.Connectivity;
import com.domaindriven.toodledo.SyncException;
import com.google.inject.Inject;
import com.softwareprojects.androidtasks.Constants;
import com.softwareprojects.androidtasks.domain.TaskAlarmManager;
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

	private class SychronizeTask extends AsyncTask<Object, Integer, AsyncTaskResult<SynchronizationResult>> {

		@Override
		protected AsyncTaskResult<SynchronizationResult> doInBackground(Object... params) {
			try {
				
				String user = preferences.getString(Constants.PREFS_TOODLEDO_USER, null);
				String pwd = preferences.getString(Constants.PREFS_TOODLEDO_PWD, null);
				String appId = preferences.getString(Constants.PREFS_TOODLEDO_APP_ID, null);
				String appToken = preferences.getString(Constants.PREFS_TOODLEDO_APP_TOKEN, null);

				synchronizer.init(user, pwd, appId, appToken);

				SynchronizationResult result = manager.sync();
				return new AsyncTaskResult<SynchronizationResult>(result);

			} catch (IOException e) {
				return new AsyncTaskResult<SynchronizationResult>(e, "Exception occurred while initializing synchronization with Toodledo.");
			} catch (SyncException e) {
				return new AsyncTaskResult<SynchronizationResult>(e, e.getMessage());
			} catch(Exception e) {
				return new AsyncTaskResult<SynchronizationResult>(e, "Catch-all exception reached");
			}
		}

		@Override
		protected void onPostExecute(AsyncTaskResult<SynchronizationResult> result) {

			Exception e = result.getError();

			if(e != null) {
				Log.e(TAG, e.getMessage(), e);
				Toast toast = Toast.makeText(getApplicationContext(), result.getMessage(), Toast.LENGTH_SHORT);
				toast.show();				
			}
			else {
				Toast toast = Toast.makeText(getApplicationContext(), result.getResult().toString(), Toast.LENGTH_SHORT);
				toast.show();

				Intent intent = new Intent("com.softwareprojects.androidtasks.TASKLISTCHANGE");
				sendBroadcast(intent);
			}

			Calendar nextSync = Calendar.getInstance();
			nextSync.add(Calendar.HOUR, 1);

			alarms.setSynchronizationAlarm(nextSync);

			super.onPostExecute(result);
		}
	}
}
