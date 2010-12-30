package com.softwareprojects.androidtasks;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;

public class Preferences extends Activity {

	Spinner weeks_before;
	Spinner weeks_future;
	CheckBox vibrate_on_notification;
	Spinner purge_age_in_weeks;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.preferences);

		weeks_before = (Spinner)findViewById(R.id.prefs_weeks_before);
		weeks_future = (Spinner)findViewById(R.id.prefs_weeks_future);
		vibrate_on_notification = (CheckBox)findViewById(R.id.prefs_vibrate);
		purge_age_in_weeks = (Spinner) findViewById(R.id.prefs_purge_age_in_weeks);
		Button ok_button = (Button)findViewById(R.id.prefs_ok_button);
		Button cancel_button = (Button)findViewById(R.id.prefs_cancel_button);

		ArrayAdapter<Integer> rangeAdapter = new ArrayAdapter<Integer>(this, 
				android.R.layout.simple_spinner_item, 
				new Integer[]{1,2,3,4,5,6});

		ArrayAdapter<Integer> purgeAdapter = new ArrayAdapter<Integer>(this, 
				android.R.layout.simple_spinner_item, 
				new Integer[]{0,1,2,3,4,5});

		rangeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		purgeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		weeks_before.setAdapter(rangeAdapter);
		weeks_future.setAdapter(rangeAdapter);
		purge_age_in_weeks.setAdapter(purgeAdapter);

		SharedPreferences prefs = getSharedPreferences("AndroidTasks", Preferences.MODE_PRIVATE);
		
		int before_item = prefs.getInt(Constants.PREFS_WEEKS_IN_PAST, 3);
		int after_item = prefs.getInt(Constants.PREFS_WEEKS_IN_FUTURE, 6);
		int purge_age_in_weeks_preference = prefs.getInt(Constants.PREFS_PURGING_TASK_AGE_IN_WEEKS, 0);
		Boolean vibrate = prefs.getBoolean(Constants.PREFS_VIBRATE_ON_NOTIFICATION, false);
		
		weeks_before.setSelection(rangeAdapter.getPosition(before_item));
		weeks_future.setSelection(rangeAdapter.getPosition(after_item));
		purge_age_in_weeks.setSelection(purgeAdapter.getPosition(purge_age_in_weeks_preference));
		vibrate_on_notification.setChecked(vibrate);
		
		cancel_button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View button) {
				setResult(RESULT_OK);
				finish();
			}
		});
		
		ok_button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View button) {
				updateSharedPreferences();
				setResult(RESULT_OK);
				finish();
			}
		});
	}
	
	private void updateSharedPreferences() {
		SharedPreferences prefs = this.getSharedPreferences("AndroidTasks", Preferences.MODE_PRIVATE);
		Editor editor = prefs.edit();
		
		editor.putInt(Constants.PREFS_WEEKS_IN_PAST, (Integer)weeks_before.getSelectedItem());
		editor.putInt(Constants.PREFS_WEEKS_IN_FUTURE, (Integer)weeks_future.getSelectedItem());
		editor.putBoolean(Constants.PREFS_VIBRATE_ON_NOTIFICATION, vibrate_on_notification.isChecked());
		editor.putInt(Constants.PREFS_PURGING_TASK_AGE_IN_WEEKS, (int) ((Integer)purge_age_in_weeks.getSelectedItem()));
		
		editor.commit();
	}
}
