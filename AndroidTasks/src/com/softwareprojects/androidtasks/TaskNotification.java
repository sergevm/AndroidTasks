package com.softwareprojects.androidtasks;

import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.inject.Inject;
import com.softwareprojects.androidtasks.domain.NotificationSource;
import com.softwareprojects.androidtasks.domain.Task;
import com.softwareprojects.androidtasks.domain.TaskDateFormatter;
import com.softwareprojects.androidtasks.domain.TaskRepository;
import com.softwareprojects.androidtasks.domain.TaskScheduler;

public class TaskNotification extends RoboActivity {

	@InjectView(R.id.notification_task_description) TextView description;
	@InjectView(R.id.notification_task_targetdate) TextView targetdate;
	@InjectView(R.id.notification_complete_checkbox) CheckBox complete;
	@InjectView(R.id.notification_snooze_period) Spinner snoozePeriod;
	@InjectView(R.id.notification_snooze_textview) TextView snooze;
	@InjectView(R.id.notification_snooze_count) TextView snoozeCount;
	@InjectView(R.id.notification_commit_button) Button commit;
	@InjectView(R.id.notification_edit_button) Button edit;

	Task task;
	long taskId;
	
	@Inject TaskScheduler scheduler;
	@Inject TaskRepository repository;
	
	private NotificationSource notificationSource;

	private final int[] snoozedMinutes = new int[] { 1, 2, 5, 10, 30, 60, 120, 180, 240, 480, 1440, 2880 };

	private static final String TAG = TaskNotification.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		Log.i(TAG, "onCreate");

		setContentView(R.layout.task_notification);

		taskId = getIntent().getLongExtra(Constants.ALARM_TASK_ID, -1);
		notificationSource = NotificationSource.valueOf(getIntent().getStringExtra(Constants.ALARM_SOURCE));
		
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, 
				R.array.snooze_periods,	android.R.layout.simple_spinner_item);
		
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		snoozePeriod.setAdapter(adapter);

		retrieveTask();
		scheduleNextReminder();

		displayTask();
		
		complete.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					snoozePeriod.setVisibility(View.GONE);
					snooze.setVisibility(View.GONE);
				} else {
					snoozePeriod.setVisibility(View.VISIBLE);
					snooze.setVisibility(View.VISIBLE);
				}
			}
		});

		commit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View button) {
				if (complete.isChecked()) {
					completeTask();
				} else {
					snoozeTask();
				}

				setResult(RESULT_OK);
				finish();
			}

			private void completeTask() {
				scheduler.complete(task);
			}

			private void snoozeTask() {
				int pos = snoozePeriod.getSelectedItemPosition();
				int snoozeTime = snoozedMinutes[pos];
				
				scheduler.snooze(task, snoozeTime, notificationSource);
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

	private void scheduleNextReminder() {
		
		switch (notificationSource) {
		case ALARMSOURCE_TARGETDATE:
		case ALARMSOURCE_REMINDERDATE:

			scheduler.createNextReminderFor(task);
			break;
		}
	}
	
	@Override
	protected void onStart() {
		Log.v(TAG, "onStart");
		super.onStart();
	}
	
	@Override
	protected void onStop() {
		Log.v(TAG, "onStop");
		super.onStop();
	}

	@Override
	protected void onDestroy() {

		Log.v(TAG, "onDestroy");
		super.onDestroy();
	}
	
	@Override
	protected void onResume() {
		Log.i(TAG, "onResume");
		repository.init();
		super.onResume();
	}
	
	private void displayTask() {
				
		if(task == null || task.isDeleted())
		{
			Toast toast = Toast.makeText(getApplicationContext(), "Task is no longer available", Toast.LENGTH_LONG);
			toast.show();
			finish();
			return;
		}
		
		if(task.isCompleted()) {
			
			snoozePeriod.setVisibility(View.GONE);
			snooze.setVisibility(View.GONE);
			complete.setEnabled(false);
			complete.setChecked(true);
			commit.setEnabled(false);
		}

		description.setText(task.getDescription());
		snoozeCount.setText(Integer.toString(task.getSnoozeCount()));

		if (task.getTargetDate() != null) {
			targetdate.setText(TaskDateFormatter.format(task.getTargetDate()));
		}
	}

	private void retrieveTask() {
		repository.init();
		
		task = repository.find(taskId);
	}

	@Override
	protected void onPause() {
		Log.i(TAG, "onPause");
		repository.flush();
		super.onPause();
	}
}
