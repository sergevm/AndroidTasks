package com.softwareprojects.androidtasks;

import android.app.Activity;
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

import com.softwareprojects.androidtasks.db.DBHelper;
import com.softwareprojects.androidtasks.db.SqliteTaskRepository;
import com.softwareprojects.androidtasks.domain.Logger;
import com.softwareprojects.androidtasks.domain.NotificationSource;
import com.softwareprojects.androidtasks.domain.RecurrenceCalculationFactory;
import com.softwareprojects.androidtasks.domain.ReminderCalculationFactory;
import com.softwareprojects.androidtasks.domain.Task;
import com.softwareprojects.androidtasks.domain.TaskDateFormatter;
import com.softwareprojects.androidtasks.domain.TaskDateProviderImpl;
import com.softwareprojects.androidtasks.domain.TaskScheduler;

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
	TaskScheduler taskScheduler;
	
	DBHelper dbHelper;
	private NotificationSource notificationSource;

	private final int[] snoozedMinutes = new int[] { 1, 2, 5, 10, 30, 60, 120, 180, 240, 480, 1440, 2880 };

	private static final String TAG = TaskNotification.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		Log.i(TAG, "onCreate");

		setContentView(R.layout.task_notification);

		description = (TextView) findViewById(R.id.notification_task_description);
		targetdate = (TextView) findViewById(R.id.notification_task_targetdate);
		complete = (CheckBox) findViewById(R.id.notification_complete_checkbox);
		snoozePeriod = (Spinner) findViewById(R.id.notification_snooze_period);
		snooze = (TextView) findViewById(R.id.notification_snooze_textview);
		snoozeCount = (TextView) findViewById(R.id.notification_snooze_count);
		commit = (Button) findViewById(R.id.notification_commit_button);
		edit = (Button) findViewById(R.id.notification_edit_button);

		long taskId = getIntent().getLongExtra(Constants.ALARM_TASK_ID, -1);
		notificationSource = NotificationSource.valueOf(getIntent().getStringExtra(Constants.ALARM_SOURCE));

		dbHelper = new DBHelper(this);
		
		taskScheduler = new TaskScheduler(
				new ReminderCalculationFactory(), 
				new RecurrenceCalculationFactory(), 
				new AndroidTaskAlarmManager(this), 
				new TaskDateProviderImpl(), 
				new SqliteTaskRepository(dbHelper), 
				new Logger());
		
		task = dbHelper.getSingle(taskId);

		switch (notificationSource) {
		case ALARMSOURCE_TARGETDATE:
		case ALARMSOURCE_REMINDERDATE:

			taskScheduler.updateReminder(task);
			break;
		}

		description.setText(task.getDescription());
		snoozeCount.setText(Integer.toString(task.getSnoozeCount()));

		if (task.getTargetDate() != null) {
			targetdate.setText(TaskDateFormatter.format(task.getTargetDate()));
		}

		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.snooze_periods,
				android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		snoozePeriod.setAdapter(adapter);

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
				taskScheduler.complete(task);
			}

			private void snoozeTask() {
				int pos = snoozePeriod.getSelectedItemPosition();
				int snoozeTime = snoozedMinutes[pos];
				
				taskScheduler.snooze(task, snoozeTime, notificationSource);
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

		Log.v(TAG, "onDestroy");
		
		if(taskScheduler != null) {
			taskScheduler = null;
		}

		if (dbHelper != null) {
			dbHelper.Cleanup();
			dbHelper = null;
		}

		super.onDestroy();
	}
}
