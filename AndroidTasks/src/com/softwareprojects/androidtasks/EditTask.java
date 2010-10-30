package com.softwareprojects.androidtasks;

import java.util.Calendar;
import java.util.Date;

import com.softwareprojects.androidtasks.db.DBHelper;
import com.softwareprojects.androidtasks.domain.Task;

import android.app.Activity;
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

public class EditTask extends Activity {
	
	private DBHelper dbHelper;
	private EditText description;
	private CheckBox completed;
	private CheckBox hasTargetDate;
	private DatePicker targetDate;
	private Button complete;
	private Button cancel;
	
	private final Calendar calendar = Calendar.getInstance();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edittask);

		dbHelper = new DBHelper(this);
		
		// Inflate view
		description = (EditText)findViewById(R.id.edit_description);
		completed = (CheckBox)findViewById(R.id.edit_completed);
		hasTargetDate = (CheckBox)findViewById(R.id.edit_has_targetDate);
		targetDate = (DatePicker)findViewById(R.id.edit_targetDate);
		complete = (Button)findViewById(R.id.edit_complete_button);
		cancel = (Button)findViewById(R.id.edit_cancel_button);
				
		// Get task passed in via Intent
		Bundle extras = getIntent().getExtras();
		final Task task = (Task)extras.getParcelable(Constants.CURRENT_TASK);

		// Use that data to fill up the widgets
		description.setText(task.description);
		completed.setChecked(task.completed);
		
		if(task.targetDate != null) {
			calendar.setTime(task.targetDate);
			hasTargetDate.setChecked(true);
			targetDate.setEnabled(true);
		}
		else {
			hasTargetDate.setChecked(false);
			targetDate.setEnabled(false);
		}
		
		hasTargetDate.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(CompoundButton hasTargetDateCheckBox, boolean isChecked) {
				if(!isChecked) {
					task.targetDate = null;
					targetDate.setEnabled(false);
				}
				else {
					task.targetDate = getTargetDate();
					targetDate.setEnabled(true);
				}
			}
			
		});
			
		targetDate.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), 
				calendar.get(Calendar.DAY_OF_MONTH), new OnDateChangedListener(){

			@Override
			public void onDateChanged(DatePicker view, int year,
					int monthOfYear, int dayOfMonth) {
				
				calendar.set(year, monthOfYear, dayOfMonth);				
				task.targetDate = calendar.getTime();
			}}
		);
		
		complete.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View view) {
				task.completed = completed.isChecked();
				task.description = description.getText().toString();
				
				dbHelper.update(task);
				
				setResult(RESULT_OK);				
				finish();
			}});

		cancel.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View view) {
				setResult(RESULT_CANCELED);
				finish();
			}});
	}
	
	private Date getTargetDate() {
		calendar.set(targetDate.getYear(), targetDate.getMonth(), targetDate.getDayOfMonth());
		return calendar.getTime();
	}
}
