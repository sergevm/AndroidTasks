package com.softwareprojects.androidtasks;

import roboguice.activity.RoboActivity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.inject.Inject;

public class Preferences extends RoboActivity {

	Spinner weeks_before;
	Spinner weeks_future;
	CheckBox vibrate_on_notification;
	Spinner purge_age_in_weeks;
	CheckBox sync_with_toodledo;
	
	@Inject SharedPreferences preferences;
		
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
		sync_with_toodledo = (CheckBox)findViewById(R.id.sync_with_toodledo_checkbox);

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

		
		int before_item = preferences.getInt(Constants.PREFS_WEEKS_IN_PAST, 3);
		int after_item = preferences.getInt(Constants.PREFS_WEEKS_IN_FUTURE, 6);
		int purge_age_in_weeks_preference = preferences.getInt(Constants.PREFS_PURGING_TASK_AGE_IN_WEEKS, 0);
		Boolean vibrate = preferences.getBoolean(Constants.PREFS_VIBRATE_ON_NOTIFICATION, false);
		
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
		
		sync_with_toodledo.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(CompoundButton checkbox, boolean checked) {
				if(checked) {
					showToodledoPasswordDialog(); 
				}
				else {
					Editor editor = preferences.edit();
					editor.putBoolean(Constants.PREFS_SYNC_WITH_TOODLEDO, false);
					editor.remove(Constants.PREFS_TOODLEDO_PWD);
					editor.commit();
				}
			}			
		});
	}
	
	private void showToodledoPasswordDialog() {
		
		final Dialog dialog = new Dialog(this);
		dialog.setContentView(R.layout.toodledo_syncsettings_dialog);
		dialog.setTitle(R.string.toodledo_syncsettings_title);
		
		final EditText pwdTextBox = (EditText)dialog.findViewById(R.id.password_textbox);
		final EditText userTextBox = (EditText)dialog.findViewById(R.id.user_textbox);
		Button okButton = (Button)dialog.findViewById(R.id.ok_button);
		
		okButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View view) {
				Editor editor = preferences.edit();
				
				String user = userTextBox.getText().toString();
				String pwd = pwdTextBox.getText().toString();
				
				if(user == null || user.length() == 0 || pwd == null || pwd.length() == 0) {
					editor.putBoolean(Constants.PREFS_SYNC_WITH_TOODLEDO, false);
					editor.remove(Constants.PREFS_TOODLEDO_PWD);
					
					sync_with_toodledo.setChecked(false);
				}
				else {
					editor.putString(Constants.PREFS_TOODLEDO_USER, user);
					editor.putString(Constants.PREFS_TOODLEDO_PWD, pwd);
					editor.putBoolean(Constants.PREFS_SYNC_WITH_TOODLEDO, true);
				}
				
				editor.commit();
				
				dialog.dismiss();
			}
			
		});
		
		String user;
		if((user = preferences.getString(Constants.PREFS_TOODLEDO_USER, null)) != null) {
			userTextBox.setText(user);
		}
				
		dialog.show();
	}

	
	private void updateSharedPreferences() {
		Editor editor = preferences.edit();
		
		editor.putInt(Constants.PREFS_WEEKS_IN_PAST, (Integer)weeks_before.getSelectedItem());
		editor.putInt(Constants.PREFS_WEEKS_IN_FUTURE, (Integer)weeks_future.getSelectedItem());
		editor.putBoolean(Constants.PREFS_VIBRATE_ON_NOTIFICATION, vibrate_on_notification.isChecked());
		editor.putInt(Constants.PREFS_PURGING_TASK_AGE_IN_WEEKS, (int) ((Integer)purge_age_in_weeks.getSelectedItem()));
		
		editor.commit();
	}
}
