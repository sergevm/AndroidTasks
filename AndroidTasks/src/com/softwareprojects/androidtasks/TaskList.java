package com.softwareprojects.androidtasks;

import java.util.Date;
import java.util.List;

import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Paint;
import android.opengl.Visibility;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.softwareprojects.androidtasks.db.DBHelper;
import com.softwareprojects.androidtasks.domain.Task;
import com.softwareprojects.androidtasks.domain.TaskDateFormatter;

public class TaskList extends ListActivity {

	private static DBHelper dbHelper;

	private final static int Filter_All = 4;
	private final static int Filter_All_In_Range = 5;
	private final static int Filter_Active = 6;
	private final static int Filter_Due = 7;
	private final static int Filter_NoDate = 8;

	private int _currentFilter;
	
	private BroadcastReceiver listChangedReceiver;
	private IntentFilter listChangedIntentFilter;
	
	private static final String TAG = TaskList.class.getSimpleName();
	
	private void setCurrentFilter(int filter) {
		if(_currentFilter != filter) {
			_currentFilter = filter;
		}
	}

	private int getCurrentFilter() {
		return _currentFilter;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.i(TAG, "onCreate");
				
		// Set up the adapter
		initializeTaskList();
				
		// Fetch the initial filter from the shared preferences
		SharedPreferences preferences = getSharedPreferences("AndroidTasks", MODE_PRIVATE);
		setCurrentFilter(preferences.getInt("ActiveFilter", Filter_All));

		// Listener for external modification to the task list
		listChangedIntentFilter = new IntentFilter("com.softwareprojects.androidtasks.TASKLISTCHANGE");
		listChangedReceiver = new TaskListChangeReceiver();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		Log.i(TAG, "onPause");
		
		SharedPreferences preferences = getSharedPreferences("AndroidTasks", MODE_PRIVATE);
		Editor prefEditor = preferences.edit();
		
		prefEditor.putInt("ActiveFilter", getCurrentFilter());
		prefEditor.commit();
		
		unregisterReceiver(listChangedReceiver);
	}

	@Override
	protected void onResume() {
		super.onResume();

		// Update the filtered list
		updateFilteredList();
		
		// Register broadcasts that inform us that the list of tasks has changed
		registerReceiver(listChangedReceiver, listChangedIntentFilter);
		
		Log.i(TAG, "onResume");
	}
	
	@Override
	protected void onStart() {
		super.onStart();

		// Initialize the database helper
		dbHelper = new DBHelper(this);

		Log.i(TAG, "onStart");
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		
		dbHelper.Cleanup();
		
		Log.i(TAG, "onStop");
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();

		Log.i(TAG, "onDestroy");
	}
	
	private void initializeTaskList() {

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
						startActivity(intent);
					}
				});
	}  

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, 1, 1, R.string.list_menu_new);
		menu.add(Menu.NONE, 2, 2, R.string.list_menu_refresh);
		menu.add(Menu.NONE, 3, 3, R.string.list_menu_prefs);
		SubMenu subMenu = menu.addSubMenu(Menu.NONE, 4, 4, R.string.list_menu_filter_header);
		subMenu.add(1, 5, 5, R.string.list_filter_all).setChecked(getCurrentFilter() == Filter_All);
		subMenu.add(1, 6, 6, R.string.list_filter_all_in_range).setChecked(getCurrentFilter() == Filter_All_In_Range);		
		subMenu.add(1, 7, 7, R.string.list_filter_active).setChecked(getCurrentFilter() == Filter_Active);
		subMenu.add(1, 8, 8, R.string.list_filter_due).setChecked(getCurrentFilter() == Filter_Due);
		subMenu.add(1, 9, 9, R.string.list_filter_nodate).setChecked(getCurrentFilter() == Filter_NoDate);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case 1:
			addTask();
			return true;
		case 2:
			refresh();
			return true;
		case 3:
			showPreferences();
			return true;
		case 5:
			setCurrentFilter(Filter_All);
			updateFilteredList();
			return true;
		case 6:
			setCurrentFilter(Filter_All_In_Range);
			updateFilteredList();
			return true;
		case 7:
			setCurrentFilter(Filter_Active);
			updateFilteredList();
			return true;
		case 8:
			setCurrentFilter(Filter_Due);
			updateFilteredList();
			return true;
		case 9:
			setCurrentFilter(Filter_NoDate);
			updateFilteredList();
		default:
			return true;
		}
	}

	private void refresh() {
		updateFilteredList();
	}

	private void addTask() {
		Intent intent = new Intent(this, EditTask.class);
		startActivityForResult(intent, 0);
	}
	
	private void showPreferences() {
		Intent intent = new Intent(this, Preferences.class);
		startActivity(intent);
	}

	private void updateFilteredList() {
		List<Task> list = null;
		
		SharedPreferences prefs = getSharedPreferences("AndroidTasks", Preferences.MODE_PRIVATE);
		int pastWeeks = prefs.getInt(Constants.PREFS_WEEKS_IN_PAST, 3);
		int futureWeeks = prefs.getInt(Constants.PREFS_WEEKS_IN_FUTURE, 6);
		
		switch(getCurrentFilter()) {
		case Filter_All:
			list = dbHelper.getAll();
			break;
		case Filter_All_In_Range:
			list = dbHelper.getAll(pastWeeks, futureWeeks);
			break;
		case Filter_Active:
			list = dbHelper.getActive(pastWeeks, futureWeeks);
			break;
		case Filter_Due:
			list = dbHelper.getDue();
			break;
		case Filter_NoDate:
			list = dbHelper.getNoDate();
		default:
			break;
		}

		TaskAdapter adapter = new TaskAdapter(this, R.layout.task_listitem, 0, list);
		setListAdapter(adapter);

		updateTitle();
	}

	private void updateTitle()
	{
		StringBuilder sb = new StringBuilder(getResources().getString(R.string.app_name));
		sb.append(" - ");
		
		switch(getCurrentFilter()) {
		case Filter_All:
			sb.append(getResources().getString(R.string.list_filter_all));
			break;
		case Filter_All_In_Range:
			sb.append(getResources().getString(R.string.list_filter_all_in_range));
			break;
		case Filter_Active:
			sb.append(getResources().getString(R.string.list_filter_active));
			break;
		case Filter_Due:
			sb.append(getResources().getString(R.string.list_filter_due));
			break;
		case Filter_NoDate:
			sb.append(getResources().getString(R.string.list_filter_nodate));
		default:
			break;
		}

		setTitle(sb.toString());
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
				ImageView image = (ImageView)view.findViewById(R.id.item_icon);
				TextView reminderdate = (TextView)view.findViewById(R.id.item_nextreminder);
				ImageView reminderimage = (ImageView)view.findViewById(R.id.item_reminder_icon);
				ImageView repeatingimage = (ImageView)view.findViewById(R.id.item_repeating_icon);

				description.setText(task.getDescription() + " (" + task.getId() + ")");

				// Strike through if task is completed
				if(task.isCompleted()) {
					description.setPaintFlags(description.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
					image.setImageResource(R.drawable.flag_green);
				}
				else {
					description.setPaintFlags(description.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);						
				}

				if(task.getTargetDate() == null) {
					targetdate.setText(R.string.no_target_date);
				} else {
					targetdate.setText(TaskDateFormatter.format(task.getTargetDate()));
					Date now = new Date();

					// Coloring
					if(task.isCompleted() == false) {
						if(now.after(task.getTargetDate())) {
							image.setImageResource(R.drawable.flag_red); 
						} else if (deadlineInLessThanADay(now, task.getTargetDate())) {
							image.setImageResource(R.drawable.flag_blue);
						}
						else {
							image.setImageResource(R.drawable.deadline);
						}
					}
				}
				
				if(task.getReminderDate() != null) {
					reminderdate.setVisibility(View.VISIBLE);
					reminderdate.setText(TaskDateFormatter.format(task.getReminderDate()));
					reminderimage.setImageResource(R.drawable.timer);
					reminderimage.setVisibility(View.VISIBLE);
				}
				else {
					reminderdate.setText(null);
					reminderdate.setVisibility(View.GONE);
					reminderimage.setImageResource(0);
					reminderimage.setVisibility(View.GONE);
				}
				
				if(task.getRecurrenceType() == Task.REPEAT_NONE) {
					repeatingimage.setImageResource(0);
					repeatingimage.setVisibility(View.GONE);
				}
				else {
					repeatingimage.setVisibility(View.VISIBLE);
					repeatingimage.setImageResource(R.drawable.repeating);
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

	private class TaskListChangeReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			updateFilteredList();
		}
		
	}
}
