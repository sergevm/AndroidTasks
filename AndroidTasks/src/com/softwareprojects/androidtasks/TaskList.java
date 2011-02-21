package com.softwareprojects.androidtasks;

import java.security.InvalidParameterException;
import java.util.Date;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.softwareprojects.androidtasks.db.SqliteToodledoRepository;
import com.softwareprojects.androidtasks.db.TasksDBHelper;
import com.softwareprojects.androidtasks.db.SqliteTaskRepository;
import com.softwareprojects.androidtasks.db.ToodledoDBHelper;
import com.softwareprojects.androidtasks.domain.Logger;
import com.softwareprojects.androidtasks.domain.RecurrenceCalculationFactory;
import com.softwareprojects.androidtasks.domain.RecurrenceCalculations;
import com.softwareprojects.androidtasks.domain.ReminderCalculationFactory;
import com.softwareprojects.androidtasks.domain.ReminderCalculations;
import com.softwareprojects.androidtasks.domain.Task;
import com.softwareprojects.androidtasks.domain.TaskAlarmManager;
import com.softwareprojects.androidtasks.domain.TaskDateFormatter;
import com.softwareprojects.androidtasks.domain.TaskDateProvider;
import com.softwareprojects.androidtasks.domain.TaskDateProviderImpl;
import com.softwareprojects.androidtasks.domain.TaskRepository;
import com.softwareprojects.androidtasks.domain.TaskScheduler;
import com.softwareprojects.androidtasks.domain.sync.SynchronizationManager;
import com.softwareprojects.androidtasks.domain.sync.SynchronizationResult;
import com.softwareprojects.androidtasks.toodledo.ToodledoRepository;
import com.softwareprojects.androidtasks.toodledo.ToodledoSynchronizer;

public class TaskList extends ListActivity {

	private static final int ACTIVITY_REQUEST_CODE_ADD = 0;
	private static final int ACTIVITY_REQUEST_CODE_PREFS = 1;

	private static final int DIALOG_CONFIRM_DELETE_ID = 0;

	private static TasksDBHelper dbHelper;

	private final static int Filter_All = 4;
	private final static int Filter_All_In_Range = 5;
	private final static int Filter_Active = 6;
	private final static int Filter_Due = 7;
	private final static int Filter_NoDate = 8;

	private int currentFilter;

	private BroadcastReceiver listChangedReceiver;
	private IntentFilter listChangedIntentFilter;

	private TaskDateProvider dates;
	private TaskRepository taskRepository;
	private TaskAlarmManager alarmManager;
	private ReminderCalculations reminders;
	private RecurrenceCalculations recurrences;
	private TaskScheduler taskScheduler;
	private boolean updatePurgingSchemeOnResume;

	private static final String TAG = TaskList.class.getSimpleName();

	private void setCurrentFilter(int filter) {
		if(currentFilter != filter) {
			currentFilter = filter;
		}
	}

	private int getCurrentFilter() {
		return currentFilter;
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

		// Register all items on the list view for context menus
		registerForContextMenu(getListView());
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

		// Update purging scheme. Ugly way to do this, but due to the fact that onActivityResult is 
		// called before onResume is called (so not all objects such as the db access helper are in 
		// acceptable state in onActivityResult)
		if(updatePurgingSchemeOnResume) {
			SharedPreferences preferences = getSharedPreferences("AndroidTasks", MODE_PRIVATE);
			int purgeAgeInWeeks = preferences.getInt(Constants.PREFS_PURGING_TASK_AGE_IN_WEEKS, 0);
			taskScheduler.purge(purgeAgeInWeeks);
			updatePurgingSchemeOnResume = false;
		}

		// Update the filtered list
		updateFilteredList();

		// Register broadcasts that inform us that the list of tasks has changed
		registerReceiver(listChangedReceiver, listChangedIntentFilter);

		Log.i(TAG, "onResume");
	}

	@Override
	protected void onStart() {
		super.onStart();

		dbHelper = new TasksDBHelper(this);
		dates = new TaskDateProviderImpl();
		taskRepository = new SqliteTaskRepository(dbHelper);
		alarmManager = new AndroidTaskAlarmManager(this);
		reminders = new ReminderCalculationFactory();
		recurrences = new RecurrenceCalculationFactory();
		taskScheduler = new TaskScheduler(reminders, recurrences, alarmManager, dates, taskRepository, new Logger());

		Log.i(TAG, "onStart");
	}

	@Override
	protected void onStop() {
		super.onStop();

		dbHelper.Cleanup();
		taskRepository = null;

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

				});
	}  

	private void editTask(Task task) {
		Intent intent = new Intent(getBaseContext(), EditTask.class);
		intent.putExtra(Constants.CURRENT_TASK, task);
		startActivity(intent);
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
		menu.add(Menu.NONE, 10, 10, "Toodledo");
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.task_list_context_menu, menu);
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
			return true;
		case 10:
			getToodledoTasks("td4d35ff02625cc", "HitTheRoadJack!");
			return true;
		default:
			return true;
		}
	}

	private void getToodledoTasks(final String user, final String password) {

		Log.d(TAG, "Syncing with Toodledo");


		ToodledoDBHelper toodledoDBHelper = new ToodledoDBHelper(this);

		try {
			ToodledoRepository toodledoRepository = new SqliteToodledoRepository(toodledoDBHelper);
			ToodledoSynchronizer syncer = new ToodledoSynchronizer(getSharedPreferences("Toodledo", MODE_PRIVATE), toodledoRepository);

			syncer.init(user, password);

			Logger logger = new Logger();

			SynchronizationManager manager = new SynchronizationManager(syncer,taskScheduler, taskRepository, logger);
			SynchronizationResult synchronizationResult = manager.sync();

			Log.d(TAG, String.format("Syncing with Toodledo completed: %s", synchronizationResult));
			
			updateFilteredList();
		}
		finally {
			toodledoDBHelper.Cleanup();
		}		
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo)item.getMenuInfo();
		Task task = (Task)getListAdapter().getItem((int)info.id);

		int menuItemId = item.getItemId();
		switch(menuItemId)
		{
		case R.id.list_context_menu_delete:
			Bundle bundle = new Bundle();
			bundle.putParcelable(Constants.CURRENT_TASK, task);
			showDialog(DIALOG_CONFIRM_DELETE_ID, bundle);
			break;
		case R.id.list_context_menu_complete:
			taskScheduler.complete(task);
			updateFilteredList();
			break;
		case R.id.list_context_menu_edit:
			editTask(task);
			break;
		}

		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);

		if(resultCode == RESULT_CANCELED) return;

		if(requestCode == ACTIVITY_REQUEST_CODE_PREFS) {
			// Ugly way to work, but necessary because onResume() has not been called here, 
			// so not possible e.g. to do database access here. This flag will be picked up 
			// in another step in the life cycle of the activity
			updatePurgingSchemeOnResume = true;
		}
	}

	@Override
	protected Dialog onCreateDialog(int id, Bundle bundle) {
		Dialog createdDialog = null;

		switch(id) {
		case DIALOG_CONFIRM_DELETE_ID:

			final Task toDelete = (Task)bundle.getParcelable(Constants.CURRENT_TASK);

			AlertDialog.Builder builder = new AlertDialog.Builder(this); 
			createdDialog = builder
			.setTitle(R.string.dialog_confirm_delete_caption)
			.setMessage(String.format(getString(R.string.dialog_confirm_delete_text), toDelete.getDescription()))
			.setPositiveButton(R.string.general_yes, new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					Log.v(TAG, String.format("Confirmed delete of task with id %d", toDelete.getId()));

					taskScheduler.delete(toDelete);
					updateFilteredList();
					removeDialog(DIALOG_CONFIRM_DELETE_ID);
				}
			})
			.setNegativeButton(R.string.general_no, new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					Log.v(TAG, String.format("Nonconfirmed delete of task with id %d", toDelete.getId()));
					removeDialog(DIALOG_CONFIRM_DELETE_ID);
				}	
			})
			.create();

			break;
		default:
			throw new InvalidParameterException("onCreateDialog is called with an invalid key");
		}

		return createdDialog;
	}

	private void refresh() {
		updateFilteredList();
	}

	private void addTask() {
		Intent intent = new Intent(this, EditTask.class);
		startActivityForResult(intent, ACTIVITY_REQUEST_CODE_ADD);
	}

	private void showPreferences() {
		Intent intent = new Intent(this, Preferences.class);
		startActivityForResult(intent, ACTIVITY_REQUEST_CODE_PREFS);
	}

	private void updateFilteredList() {
		List<Task> list = null;

		SharedPreferences prefs = getSharedPreferences("AndroidTasks", Preferences.MODE_PRIVATE);
		int pastWeeks = prefs.getInt(Constants.PREFS_WEEKS_IN_PAST, 3);
		int futureWeeks = prefs.getInt(Constants.PREFS_WEEKS_IN_FUTURE, 6);

		switch(getCurrentFilter()) {
		case Filter_All:
			list = taskRepository.getAll();
			break;
		case Filter_All_In_Range:
			list = taskRepository.getAll(pastWeeks, futureWeeks);
			break;
		case Filter_Active:
			list = taskRepository.getActive(pastWeeks, futureWeeks);
			break;
		case Filter_Due:
			list = taskRepository.getDue();
			break;
		case Filter_NoDate:
			// TODO: move to repository
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

