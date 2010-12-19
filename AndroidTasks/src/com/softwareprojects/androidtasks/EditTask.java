package com.softwareprojects.androidtasks;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.softwareprojects.androidtasks.db.DBHelper;
import com.softwareprojects.androidtasks.db.SqliteTaskRepository;
import com.softwareprojects.androidtasks.domain.Logger;
import com.softwareprojects.androidtasks.domain.RecurrenceCalculationFactory;
import com.softwareprojects.androidtasks.domain.ReminderCalculationFactory;
import com.softwareprojects.androidtasks.domain.Task;
import com.softwareprojects.androidtasks.domain.TaskDateProvider;
import com.softwareprojects.androidtasks.domain.TaskDateProviderImpl;
import com.softwareprojects.androidtasks.domain.TaskScheduler;

public class EditTask extends Activity {

	private DBHelper dbHelper;
	private EditText description;
	private CheckBox completed;
	private CheckBox hasTargetDate;
	private EditText notes;
	private EditText location;
	private Button targetDateButton;
	private Button targetTimeButton;
	private Button complete;
	private Button cancel;
	private Spinner reminderType;
	private TextView reminderTypeLabel;

	private Task task;

	private final Context context = this;

	private TaskScheduler taskScheduler;

	private static final TaskDateProvider dates = new TaskDateProviderImpl();
	private static final String TAG = EditTask.class.getSimpleName();
	private static final Calendar calendar = Calendar.getInstance();
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT_STRING);
	private static final SimpleDateFormat timeFormat = new SimpleDateFormat(Constants.TIME_FORMAT_STRING);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.i(TAG, "onCreate");

		// Associate a layout with this activity ...
		setContentView(R.layout.edittask);

		// Initialize the calendar so it doesn't specify time ...
		calendar.setTime(dates.getNow().getTime());

		// Inflate view
		description = (EditText) findViewById(R.id.edit_description);
		completed = (CheckBox) findViewById(R.id.edit_completed);
		hasTargetDate = (CheckBox) findViewById(R.id.edit_has_targetDate);
		notes = (EditText) findViewById(R.id.edit_notes);
		location = (EditText) findViewById(R.id.edit_location);
		complete = (Button) findViewById(R.id.edit_commit_button);
		cancel = (Button) findViewById(R.id.edit_cancel_button);
		targetDateButton = (Button) findViewById(R.id.edit_targetDate_button);
		targetTimeButton = (Button) findViewById(R.id.edit_targetTime_button);
		reminderType = (Spinner) findViewById(R.id.edit_reminder_type);
		reminderTypeLabel = (TextView) findViewById(R.id.edit_reminder_type_label);

		hasTargetDate.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton hasTargetDateCheckBox, boolean isChecked) {
				if (!isChecked) {
					task.setTargetDate(null);
				} else {
					task.setTargetDate(getTargetDate());
				}

				updateTargetDateFrom(task.getTargetDate());
				updateTargetDateControlsFrom(task);
			}
		});

		targetDateButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View button) {

				DatePickerDialog dialog = new DatePickerDialog(context, listener, calendar.get(Calendar.YEAR), calendar
						.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

				dialog.show();
			}

			OnDateSetListener listener = new OnDateSetListener() {

				@Override
				public void onDateSet(DatePicker picker, int year, int monthOfYear, int dayOfMonth) {
					calendar.set(year, monthOfYear, dayOfMonth);
					task.setTargetDate(calendar.getTime());
					updateTargetDateFrom(task.getTargetDate());
				}
			};
		});

		targetTimeButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View button) {
				TimePickerDialog dialog = new TimePickerDialog(context, listener, calendar.get(Calendar.HOUR_OF_DAY),
						calendar.get(Calendar.MINUTE), false);

				dialog.show();
			}

			OnTimeSetListener listener = new OnTimeSetListener() {

				@Override
				public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
					calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
					calendar.set(Calendar.MINUTE, minute);
					task.setTargetDate(calendar.getTime());
					updateTargetDateFrom(task.getTargetDate());
				}
			};
		});

		complete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				task.setCompleted(completed.isChecked());
				task.setDescription(description.getText().toString());
				task.setNotes(notes.getText().toString());
				task.setLocation(location.getText().toString());
				task.setReminderType(reminderType.getSelectedItemPosition());

				if (completed.isChecked()) {
					taskScheduler.complete(task);
				}

				taskScheduler.schedule(task);

				setResult(RESULT_OK);
				finish();
			}
		});

		completed.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				CheckBox checkbox = (CheckBox) view;
				task.setCompleted(checkbox.isChecked());
				updateTargetDateControlsFrom(task);
			}
		});

		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				setResult(RESULT_CANCELED);
				finish();
			}
		});

		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.reminder_types,
				android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		reminderType.setAdapter(adapter);

	}

	@Override
	protected void onStart() {
		super.onStart();

		Log.i(TAG, "onStart");
		dbHelper = new DBHelper(this);

		taskScheduler = new TaskScheduler(new ReminderCalculationFactory(), 
				new RecurrenceCalculationFactory(),	new AndroidTaskAlarmManager(this), 
				dates, new SqliteTaskRepository(dbHelper), new Logger());
	}

	@Override
	protected void onStop() {
		super.onStop();

		Log.i(TAG, "onStop");
		taskScheduler = null;
		dbHelper.Cleanup();
	}

	@Override
	protected void onResume() {
		super.onResume();

		Log.i(TAG, "onResume");

		// Get task passed in via Intent
		task = getOrCreateTask();

		// Use that data to fill up the widgets
		description.setText(task.getDescription());
		completed.setChecked(task.isCompleted());
		notes.setText(task.getNotes());
		location.setText(task.getLocation());
		reminderType.setSelection(task.getReminderType());

		updateTargetDateFrom(task);
		updateTargetDateControlsFrom(task);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		Log.i(TAG, "onDestroy");

		dbHelper = null;
	}

	private Task getOrCreateTask() {
		if (getIntent().hasExtra(Constants.CURRENT_TASK)) {

			Bundle extras = getIntent().getExtras();
			return (Task) extras.getParcelable(Constants.CURRENT_TASK);
		} else if (getIntent().hasExtra(Constants.ALARM_TASK_ID)) {
			long dueTaskId = getIntent().getLongExtra(Constants.ALARM_TASK_ID, -1);
			Task task = dbHelper.getSingle(dueTaskId);
			return task;
		} else {
			Task newTask = new Task();
			return newTask;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		menu.add(Menu.NONE, 0, 0, R.string.edit_options_menu_recurrence);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		Intent intent = new Intent(this, TaskRecurrence.class);
		intent.putExtra(Constants.CURRENT_TASK, task);

		startActivityForResult(intent, 0);

		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == 0 && resultCode == RESULT_OK) {
			int recurrenceType = data.getIntExtra("RecurrenceType", Task.REPEAT_NONE);
			int recurrenceValue = data.getIntExtra("RecurrenceValue", 0);

			task.repeats(recurrenceType, recurrenceValue);
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	protected Date getTargetDate() {
		return calendar.getTime();
	}

	private void updateTargetDateControlsFrom(final Task task) {

		hasTargetDate.setChecked(task.getTargetDate() != null);
		enableTargetDateControls(task);
	}

	private void enableTargetDateControls(final Task task) {
		hasTargetDate.setEnabled(task.isCompleted() == false);
		targetDateButton.setVisibility(task.isCompleted() == false & task.getTargetDate() != null ? View.VISIBLE
				: View.INVISIBLE);
		targetTimeButton.setVisibility(task.isCompleted() == false & task.getTargetDate() != null ? View.VISIBLE
				: View.INVISIBLE);
		reminderType.setVisibility(task.canHaveReminder() ? View.VISIBLE : View.GONE);
		reminderTypeLabel.setVisibility(task.canHaveReminder() ? View.VISIBLE : View.GONE);
	}

	private void updateTargetDateFrom(final Task task) {
		updateTargetDateFrom(task.getTargetDate());
	}

	private void updateTargetDateFrom(final Date date) {
		if (date == null) {
			targetDateButton.setText(null);
			targetTimeButton.setText(null);
			Calendar today = Calendar.getInstance();
			calendar.set(today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
		} else {
			targetDateButton.setText(dateFormat.format(date));
			targetTimeButton.setText(timeFormat.format(date));
			calendar.setTime(date);
		}
	}
}
