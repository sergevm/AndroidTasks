package com.softwareprojects.androidtasks;

import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.softwareprojects.androidtasks.db.DBHelper;
import com.softwareprojects.androidtasks.domain.Task;

public class TaskList extends ListActivity {

	private static DBHelper dbHelper;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		dbHelper = new DBHelper(this);

		bindTaskList();
	}

	private void bindTaskList() {
		List<Task> tasks = getTasks(); 

		ArrayAdapter<Task> adapter = 
			new ArrayAdapter<Task>(this, R.layout.task_listitem, tasks);

		this.getListView().setOnItemClickListener(
				new AdapterView.OnItemClickListener(){

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {

						Task task = (Task)parent.getItemAtPosition(position);					
						editTask(task);

					}

					private void editTask(Task task) {
						Intent intent = new Intent(getBaseContext(), EditTask.class);
						intent.putExtra(Constants.CURRENT_TASK, task);
						startActivityForResult(intent, 0);
					}

					@SuppressWarnings("unused")
					private void showDebuggingToast(AdapterView<?> parent,
							int position) {
						Task task = (Task)parent.getItemAtPosition(position);
						Toast.makeText(getBaseContext(), task.description, 
								Toast.LENGTH_SHORT).show();
					}
				});

		setListAdapter(adapter);
	}  

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch(resultCode) {
		case RESULT_OK:
			bindTaskList();
		default:
			break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		/*
    	TODO:
    		- New task
    		- Sync
    		- ...
		 */
		return super.onCreateOptionsMenu(menu);
	}

	private List<Task> getTasks()
	{
		return dbHelper.getAll();
	}
}
