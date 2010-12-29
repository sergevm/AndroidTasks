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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.preferences);

		weeks_before = (Spinner)findViewById(R.id.prefs_weeks_before);
		weeks_future = (Spinner)findViewById(R.id.prefs_weeks_future);
		vibrate_on_notification = (CheckBox)findViewById(R.id.prefs_vibrate);
		Button ok_button = (Button)findViewById(R.id.prefs_ok_button);
		Button cancel_button = (Button)findViewById(R.id.prefs_cancel_button);

		ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(this, 
				android.R.layout.simple_spinner_item, 
				new CharSequence[]{"1", "2", "3", "4", "5", "6"});
		
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		weeks_before.setAdapter(adapter);
		weeks_future.setAdapter(adapter);

		SharedPreferences prefs = getSharedPreferences("AndroidTasks", Preferences.MODE_PRIVATE);
		
		String before_item = String.valueOf(prefs.getInt(Constants.PREFS_WEEKS_IN_PAST, 3));
		String after_item = String.valueOf(prefs.getInt(Constants.PREFS_WEEKS_IN_FUTURE, 6));
		Boolean vibrate = prefs.getBoolean(Constants.PREFS_VIBRATE_ON_NOTIFICATION, false);
		
		weeks_before.setSelection(adapter.getPosition(before_item));
		weeks_future.setSelection(adapter.getPosition(after_item));
		vibrate_on_notification.setChecked(vibrate);
		
		cancel_button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View button) {
				finish();
			}
		});
		
		ok_button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View button) {
				updateSharedPreferences();
				finish();
			}
		});
	}
	
	private void updateSharedPreferences() {
		SharedPreferences prefs = this.getSharedPreferences("AndroidTasks", Preferences.MODE_PRIVATE);
		Editor editor = prefs.edit();
		
		editor.putInt(Constants.PREFS_WEEKS_IN_PAST, Integer.parseInt((String)weeks_before.getSelectedItem()));
		editor.putInt(Constants.PREFS_WEEKS_IN_FUTURE, Integer.parseInt((String)weeks_future.getSelectedItem()));
		editor.putBoolean(Constants.PREFS_VIBRATE_ON_NOTIFICATION, vibrate_on_notification.isChecked());
		
		editor.commit();
	}
}
