package com.softwareprojects.androidtasks;

import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;
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

	@InjectView(R.id.prefs_weeks_before) Spinner weeks_before;
	@InjectView(R.id.prefs_weeks_future) Spinner weeks_future;
	@InjectView(R.id.prefs_vibrate) CheckBox vibrate_on_notification;
	@InjectView(R.id.prefs_purge_age_in_weeks) Spinner purge_age_in_weeks;
	@InjectView(R.id.sync_with_toodledo_checkbox) CheckBox sync_with_toodledo;
	@InjectView(R.id.prefs_ok_button) Button ok_button;
	@InjectView(R.id.prefs_cancel_button) Button cancel_button;
	
	@Inject SharedPreferences preferences;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.preferences);

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
		Boolean sync = preferences.getBoolean(Constants.PREFS_SYNC_WITH_TOODLEDO, false);
		
		weeks_before.setSelection(rangeAdapter.getPosition(before_item));
		weeks_future.setSelection(rangeAdapter.getPosition(after_item));
		purge_age_in_weeks.setSelection(purgeAdapter.getPosition(purge_age_in_weeks_preference));
		vibrate_on_notification.setChecked(vibrate);
		sync_with_toodledo.setChecked(sync);
		
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
		final EditText appIdTextBox = (EditText)dialog.findViewById(R.id.app_id_textbox);
		final EditText appTokenTextBox = (EditText)dialog.findViewById(R.id.app_token_textbox);
		Button okButton = (Button)dialog.findViewById(R.id.ok_button);
		
		okButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View view) {
				Editor editor = preferences.edit();
				
				String user = userTextBox.getText().toString();
				String pwd = pwdTextBox.getText().toString();
				String appId = appIdTextBox.getText().toString();
				String appToken = appTokenTextBox.getText().toString();
				
				if(user == null || user.length() == 0 || pwd == null || pwd.length() == 0) {
					editor.putBoolean(Constants.PREFS_SYNC_WITH_TOODLEDO, false);
					editor.remove(Constants.PREFS_TOODLEDO_PWD);
					
					sync_with_toodledo.setChecked(false);
				}
				else {
					editor.putString(Constants.PREFS_TOODLEDO_USER, user);
					editor.putString(Constants.PREFS_TOODLEDO_PWD, pwd);
					editor.putString(Constants.PREFS_TOODLEDO_APP_ID, appId);
					editor.putString(Constants.PREFS_TOODLEDO_APP_TOKEN, appToken);
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
		
		String appId;
		if((appId = preferences.getString(Constants.PREFS_TOODLEDO_APP_ID, null)) != null) {
			appIdTextBox.setText(appId);
		}
		
		String appToken;
		if((appToken = preferences.getString(Constants.PREFS_TOODLEDO_APP_TOKEN, null)) != null) {
			appTokenTextBox.setText(appToken);
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
