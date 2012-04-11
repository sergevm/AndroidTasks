package com.softwareprojects.androidtasks;

import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.softwareprojects.androidtasks.domain.Task;

public class TaskRecurrence extends RoboActivity {

	@InjectView(R.id.recurrence_type) Spinner spinner;
	@InjectView(R.id.recurrency_value) EditText recurrenceValue;
	@InjectView(R.id.recurrence_confirm) Button confirmButton;
	@InjectView(R.id.recurrence_cancel) Button cancelButton;

	Task task;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.task_recurrence);

		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.recurrence_types, android.R.layout.simple_spinner_item);

		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		spinner.setAdapter(adapter);

		task = (Task) getIntent().getExtras().getParcelable(Constants.CURRENT_TASK);

		// Uses the convention that the constant values for recurrence types
		// represent the position in the available options
		spinner.setSelection(task.getRecurrenceType());
		recurrenceValue.setText(new Integer(task.getRecurrenceValue()).toString());

		confirmButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View confirmButton) {

				Intent intent = new Intent();
				intent.putExtra("RecurrenceType", spinner.getSelectedItemPosition());
				intent.putExtra("RecurrenceValue", parseRecurrenceValue());

				setResult(RESULT_OK, intent);
				finish();
			}
		});

		cancelButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View cancelButton) {
				setResult(RESULT_CANCELED);
				finish();
			}
		});
		
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				if(spinner.getSelectedItemPosition() == 0) {
					recurrenceValue.setText("0");
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {				
			}
			
		});
	}
	
	private int parseRecurrenceValue() {
		return Integer.parseInt(recurrenceValue.getText().toString());
	}
}
