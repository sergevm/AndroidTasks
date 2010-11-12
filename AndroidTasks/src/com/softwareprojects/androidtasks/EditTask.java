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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import com.softwareprojects.androidtasks.db.DBHelper;
import com.softwareprojects.androidtasks.domain.Task;

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

	private final Context context = this;
	private static final Calendar calendar = Calendar.getInstance();
	private TaskAlarmManager alarmManager;
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT_STRING);
	private static final SimpleDateFormat timeFormat = new SimpleDateFormat(Constants.TIME_FORMAT_STRING);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edittask);

		dbHelper = new DBHelper(this);
		
		// Initialize the alarm manager
		alarmManager = new TaskAlarmManager(this);

		// Initialize the calendar so it doesn't specify time ...
		calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE),0 ,0 ,0);

		// Inflate view
		description = (EditText)findViewById(R.id.edit_description);
		completed = (CheckBox)findViewById(R.id.edit_completed);
		hasTargetDate = (CheckBox)findViewById(R.id.edit_has_targetDate);
		notes = (EditText)findViewById(R.id.edit_notes);
		location = (EditText)findViewById(R.id.edit_location);
		complete = (Button)findViewById(R.id.edit_commit_button);
		cancel = (Button)findViewById(R.id.edit_cancel_button);
		targetDateButton = (Button)findViewById(R.id.edit_targetDate_button);
		targetTimeButton = (Button)findViewById(R.id.edit_targetTime_button);

		// Get task passed in via Intent
		final Task task = getOrCreateTask();

		// Use that data to fill up the widgets
		description.setText(task.description);
		completed.setChecked(task.completed);
		notes.setText(task.notes);
		location.setText(task.location);

		updateTargetDateFrom(task);
		updateTargetDateControlsFrom(task);

		hasTargetDate.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(CompoundButton hasTargetDateCheckBox, boolean isChecked) {
				if(!isChecked) {
					task.targetDate = null;
				}
				else {
					task.targetDate = getTargetDate();
				}
				
				updateTargetDateFrom(task.targetDate);
				updateTargetDateControlsFrom(task);
			}
		});

		targetDateButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View button) {

				DatePickerDialog dialog = new DatePickerDialog(context, 
						listener, 
						calendar.get(Calendar.YEAR), 
						calendar.get(Calendar.MONTH), 
						calendar.get(Calendar.DAY_OF_MONTH));

				dialog.show();
			}

			OnDateSetListener listener = new OnDateSetListener() {

				@Override
				public void onDateSet(DatePicker picker, int year, int monthOfYear,
						int dayOfMonth) {
					calendar.set(year, monthOfYear, dayOfMonth);
					task.targetDate = calendar.getTime();
					updateTargetDateFrom(task.targetDate);
				}
			};
		});
		
		targetTimeButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View button) {
				TimePickerDialog dialog = new TimePickerDialog(context, listener,
						calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false); 
				
				dialog.show();
			}
			
			OnTimeSetListener listener = new OnTimeSetListener() {
				
				@Override
				public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
					calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
					calendar.set(Calendar.MINUTE, minute);
					task.targetDate = calendar.getTime();
					updateTargetDateFrom(task.targetDate);
				}
			};
		});

		complete.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View view) {
				task.completed = completed.isChecked();
				task.description = description.getText().toString();
				task.notes = notes.getText().toString();
				task.location = location.getText().toString();

				if(task.id == 0) {
					dbHelper.insert(task);
				}
				else {
					dbHelper.update(task);
				}

				alarmManager.setAlarm(task);
				
				setResult(RESULT_OK);				
				finish();
			}});

		completed.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				CheckBox checkbox = (CheckBox)view;
				task.completed = checkbox.isChecked();
				updateTargetDateControlsFrom(task);
			}});

		cancel.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View view) {
				setResult(RESULT_CANCELED);
				finish();
			}});
	}

	private Task getOrCreateTask() {
		if(getIntent().hasExtra(Constants.CURRENT_TASK)) { // Activity was started due to selection in task list ...
			Bundle extras = getIntent().getExtras();
			return (Task)extras.getParcelable(Constants.CURRENT_TASK);
		}
		else if(getIntent().hasExtra(Constants.ALARM_TASK_ID)) { // Activity was started due to notification click ...
			long dueTaskId = getIntent().getLongExtra(Constants.ALARM_TASK_ID, -1);
			Task task = dbHelper.getSingle(dueTaskId);
			return task;
		}
		else { // No task passed in ... Must be the creation of a new task ...
			Task newTask = new Task();
			return newTask;
		}
	}

	protected Date getTargetDate() {
		return calendar.getTime();
	}

	private void updateTargetDateControlsFrom(final Task task) {

		hasTargetDate.setChecked(task.targetDate != null);
		enableTargetDateControls(task);
	}

	private void enableTargetDateControls(final Task task) {
		hasTargetDate.setEnabled(task.completed == false);
		targetDateButton.setVisibility(task.completed == false & task.targetDate != null ? View.VISIBLE : View.INVISIBLE);
		targetTimeButton.setVisibility(task.completed == false & task.targetDate != null ? View.VISIBLE : View.INVISIBLE);
	}

	private void updateTargetDateFrom(final Task task) {
		updateTargetDateFrom(task.targetDate);
	}

	private void updateTargetDateFrom(final Date date) {
		if(date == null) {
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
