package com.softwareprojects.androidtasks;

import java.text.SimpleDateFormat;

import com.softwareprojects.androidtasks.db.DBHelper;
import com.softwareprojects.androidtasks.domain.Task;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TextView;

public class TaskNotification extends Activity {

	TextView description;
	TextView targetdate;
	CheckBox complete;
	Spinner snoozePeriod;
	TextView snooze;
	TextView snoozeCount;
	Button commit;
	Button edit;
	
	Task task;
	
	DBHelper dbHelper;
	private TaskAlarmManager alarmManager;
	
	private final SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DATETIME_FORMAT_STRING);
	private final Integer[] snoozedMinutes = new Integer[]{1, 2, 5, 10, 30, 60, 120, 180, 240, 480, 1440, 2880};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.task_notification);

		description = (TextView)findViewById(R.id.notification_task_description);
		targetdate = (TextView)findViewById(R.id.notification_task_targetdate);
		complete = (CheckBox)findViewById(R.id.notification_complete_checkbox);
		snoozePeriod = (Spinner)findViewById(R.id.notification_snooze_period);
		snooze = (TextView)findViewById(R.id.notification_snooze_textview);
		snoozeCount = (TextView)findViewById(R.id.notification_snooze_count);
		commit = (Button)findViewById(R.id.notification_commit_button);
		edit = (Button)findViewById(R.id.notification_edit_button);
		
		long taskId = getIntent().getLongExtra(Constants.ALARM_TASK_ID, -1);
	
		dbHelper = new DBHelper(this);
		task = dbHelper.getSingle(taskId);
		
		alarmManager = new TaskAlarmManager(this);
		
		description.setText(task.description);
		targetdate.setText(dateFormat.format(task.targetDate));
		snoozeCount.setText(Integer.toString(task.snoozeCount));
		
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.snooze_periods, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		snoozePeriod.setAdapter(adapter);
		
		complete.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,	boolean isChecked) {
				if(isChecked) {
					snoozePeriod.setVisibility(View.GONE);
					snooze.setVisibility(View.GONE);
				}
				else {
					snoozePeriod.setVisibility(View.VISIBLE);
					snooze.setVisibility(View.VISIBLE);					
				}
			}
		});
				
		commit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View button) {
				if(complete.isChecked()) {
					completeTask();
				}
				else {
					snoozeTask();
				}

				setResult(RESULT_OK);
				finish();
			}

			private void completeTask() {
				task.completed = true;
				dbHelper.update(task);
			}

			private void snoozeTask() {
				// create a new alarm ...
				Integer pos = snoozePeriod.getSelectedItemPosition();
				Integer snoozeTime = snoozedMinutes[pos];
				alarmManager.snoozeAlarm(task, snoozeTime);
				
				// update the snooze count ...
				task.snoozeCount++;
				dbHelper.update(task);
			};
		});
		
		edit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), EditTask.class);
				intent.putExtra(Constants.CURRENT_TASK, task);
				startActivity(intent);
				
				finish();
			}			
		});
	}
	
	@Override
	protected void onDestroy() {

		if(dbHelper != null) {
			dbHelper.Cleanup();
			dbHelper = null;
		}
		
		super.onDestroy();
	}
}
