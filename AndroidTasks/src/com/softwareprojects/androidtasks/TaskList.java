package com.softwareprojects.androidtasks;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.softwareprojects.androidtasks.db.DBHelper;
import com.softwareprojects.androidtasks.domain.Task;

public class TaskList extends ListActivity {

	private static DBHelper dbHelper;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		dbHelper = new DBHelper(this);

		bindTaskList();
	}

	private void bindTaskList() {

		List<Task> tasks = getTasks(); 

		TaskAdapter adapter = new TaskAdapter(this, R.layout.task_listitem, 0, tasks);
		
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
		menu.add(Menu.NONE, 1, 1, "New");
		menu.add(Menu.NONE, 2, 2, "Sync");
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case 1:
			addTask();
			return true;
		case 2:
			sync();
			return true;
		default:
			return true;
		}
	}

	private void sync() {
		
	}

	private void addTask() {
		Intent intent = new Intent(this, EditTask.class);
		startActivityForResult(intent, 0);
	}

	private List<Task> getTasks()
	{
		return dbHelper.getAll();
	}
	
	private class TaskAdapter extends ArrayAdapter<Task> {

		List<Task> tasks;
		
		public TaskAdapter(Context context, int resource, int textViewResourceId,
				List<Task> objects) {
			super(context, resource, textViewResourceId, objects);
			this.tasks = objects;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			
			if(view == null) {
				LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = inflater.inflate(R.layout.task_listitem, null);
			}
			
			Task task = tasks.get(position);
			
			if(task != null) {
				
				TextView description = (TextView)view.findViewById(R.id.item_description);
				TextView targetdate = (TextView)view.findViewById(R.id.item_targetdate);
				
				description.setText(task.description);
				
				// Strike through if task is completed
				if(task.completed) {
					description.setPaintFlags(description.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
				}
				else {
					description.setPaintFlags(description.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);						
				}

				if(task.targetDate == null) {
					targetdate.setText("no target date");
				} else {
					
					SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT_STRING);
					targetdate.setText(dateFormat.format(task.targetDate));
					
					Date now = new Date();
					
					// Coloring
					if(now.after(task.targetDate)) {
						view.setBackgroundColor(Color.RED);
						targetdate.setTypeface(Typeface.DEFAULT_BOLD);
					} else if (deadlineInLessThanADay(now, task.targetDate)) {
						targetdate.setTextColor(Color.RED);
						view.setBackgroundColor(Color.DKGRAY);
						targetdate.setTypeface(Typeface.DEFAULT_BOLD);						
					}
				}
			}
			
			return view;
		}

		private boolean deadlineInLessThanADay(Date left, Date right) {
			Date smallest = left.before(right) ? left : right;
			Date biggest = left.before(right) ? right :left;
			
			long diff = biggest.getTime() - smallest.getTime();
			long days = diff / (1000 * 60 * 60 * 24);
			
			return days == 0;
		}
	}
}
