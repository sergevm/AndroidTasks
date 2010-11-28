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
import com.softwareprojects.androidtasks.domain.NotificationSource;
import com.softwareprojects.androidtasks.domain.ReminderFactory;
import com.softwareprojects.androidtasks.domain.ReminderFactoryImpl;
import com.softwareprojects.androidtasks.domain.Task;
import com.softwareprojects.androidtasks.domain.TaskAlarmManager;
import com.softwareprojects.androidtasks.domain.TaskDateFormatter;
import com.softwareprojects.androidtasks.domain.TaskDateProvider;
import com.softwareprojects.androidtasks.domain.TaskDateProviderImpl;

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
	private ReminderFactory reminders;
	private static final TaskDateProvider dates = new TaskDateProviderImpl();
	private NotificationSource notificationSource;

	private final int[] snoozedMinutes = new int[] { 1, 2, 5, 10, 30, 60, 120,
			180, 240, 480, 1440, 2880 };

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
		notificationSource = NotificationSource.valueOf(getIntent()
				.getStringExtra(Constants.ALARM_SOURCE));

		alarmManager = new TaskAlarmManagerImpl(this, dates);
		reminders = new ReminderFactoryImpl();

		dbHelper = new DBHelper(this);
		task = dbHelper.getSingle(taskId);

		switch (notificationSource) {
		case ALARMSOURCE_TARGETDATE:
		case ALARMSOURCE_REMINDERDATE:


			task.updateReminder(alarmManager, reminders, dates);
			dbHelper.update(task);

			Log.i(TAG,
					"Updated alarms for the next reminder on task with id "
					+ task.getId() + " on "
					+ TaskDateFormatter.format(task.getReminderDate()));

			break;
		}

		description.setText(task.getDescription());
		snoozeCount.setText(Integer.toString(task.getSnoozeCount()));

		if(task.getTargetDate() != null) {
			targetdate.setText(TaskDateFormatter.format(task.getTargetDate()));
		}

		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.snooze_periods,
				android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		snoozePeriod.setAdapter(adapter);

		complete.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
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
				task.complete(alarmManager);
				dbHelper.update(task);
			}

			private void snoozeTask() {
				// create a new alarm ...
				int pos = snoozePeriod.getSelectedItemPosition();
				int snoozeTime = snoozedMinutes[pos];
				task.snooze(alarmManager, dates, snoozeTime, notificationSource);

				// update the count ...
				task.setSnoozeCount(task.getSnoozeCount() + 1);
				dbHelper.update(task);
			};
		});

		edit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(),
						EditTask.class);
				intent.putExtra(Constants.CURRENT_TASK, task);
				startActivity(intent);

				finish();
			}
		});
	}

	@Override
	protected void onDestroy() {

		Log.i(TAG, "onDestroy");

		if (dbHelper != null) {
			dbHelper.Cleanup();
			dbHelper = null;
		}

		super.onDestroy();
	}
}
