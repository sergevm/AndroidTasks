package com.softwareprojects.androidtasks;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.softwareprojects.androidtasks.db.DBHelper;
import com.softwareprojects.androidtasks.domain.Task;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.EditText;
import android.widget.TextView;

public class EditTask extends Activity {

	private DBHelper dbHelper;
	private EditText description;
	private CheckBox completed;
	private CheckBox hasTargetDate;
	private TextView targetDateText;
	private Button setTargetDateButton;
	private Button complete;
	private Button cancel;

	private final Context context = this;
	private static final Calendar calendar = Calendar.getInstance();
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT_STRING);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edittask);

		dbHelper = new DBHelper(this);

		// Inflate view
		description = (EditText)findViewById(R.id.edit_description);
		completed = (CheckBox)findViewById(R.id.edit_completed);
		hasTargetDate = (CheckBox)findViewById(R.id.edit_has_targetDate);
		complete = (Button)findViewById(R.id.edit_commit_button);
		cancel = (Button)findViewById(R.id.edit_cancel_button);
		targetDateText = (TextView)findViewById(R.id.edit_targetDate_textview);
		setTargetDateButton = (Button)findViewById(R.id.edit_targetDate_button);

		// Get task passed in via Intent
		final Task task = getOrCreateTask();

		// Use that data to fill up the widgets
		description.setText(task.description);
		completed.setChecked(task.completed);

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

		setTargetDateButton.setOnClickListener(new OnClickListener(){

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

		complete.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View view) {
				task.completed = completed.isChecked();
				task.description = description.getText().toString();

				if(task.id == 0) {
					dbHelper.insert(task);
				}
				else {
					dbHelper.update(task);
				}

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
		if(getIntent().hasExtra(Constants.CURRENT_TASK)) {
			Bundle extras = getIntent().getExtras();
			return (Task)extras.getParcelable(Constants.CURRENT_TASK);
		}
		else {
			return new Task();
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
		setTargetDateButton.setEnabled(task.completed == false & task.targetDate != null);
	}

	private void updateTargetDateFrom(final Task task) {
		updateTargetDateFrom(task.targetDate);
	}

	private void updateTargetDateFrom(final Date date) {
		if(date == null) {
			targetDateText.setText(null);
			Calendar today = Calendar.getInstance();
			calendar.set(today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH));
		} else {
			targetDateText.setText(dateFormat.format(date));
			calendar.setTime(date);		
		}
	}
}
