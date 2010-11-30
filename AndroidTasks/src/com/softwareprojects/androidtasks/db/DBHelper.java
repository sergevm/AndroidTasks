package com.softwareprojects.androidtasks.db;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.softwareprojects.androidtasks.Constants;
import com.softwareprojects.androidtasks.domain.Task;

public class DBHelper {
	// DB names
	private static final String DB_NAME = "AndroidTasks";
	private static final String DB_TASKS_TABLE = "Tasks";
	private static int DB_VERSION = 3;

	private static final String[] DB_TASKS_COLS = 
		new String[]{"id", "description", "completed", "targetdate", "snoozecount", "notes", "location", "remindertype", "reminderdate"};

	// Logging stuff
	private static final String CLASSNAME = DBHelper.class.getSimpleName();

	// Instance of the database
	private SQLiteDatabase db;

	// Factory for db instance
	private DBOpenHelper dbOpenHelper; 

	private static class DBOpenHelper extends SQLiteOpenHelper
	{
		// SQL statement that creates the TASKS table 
		private static final String DB_CREATE_TASKS_TABLE = "CREATE TABLE " + 
		DBHelper.DB_TASKS_TABLE + " (id INTEGER PRIMARY KEY, description TEXT, " + 
		"completed INTEGER, targetdate TEXT, snoozecount INTEGER DEFAULT 0, notes TEXT, location TEXT, remindertype INTEGER DEFAULT 0, reminderdate TEXT);";

		public DBOpenHelper(Context context) {
			super(context, DBHelper.DB_NAME, null, DBHelper.DB_VERSION);

		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			try{
				db.execSQL(DB_CREATE_TASKS_TABLE);
			}
			catch(SQLException e){
				Log.e(Constants.LOGTAG, DBHelper.CLASSNAME, e);
			}
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + DBHelper.DB_TASKS_TABLE);
			this.onCreate(db);
		}
	}

	public DBHelper(Context context)
	{
		this.dbOpenHelper = new DBOpenHelper(context);
		this.establishDb();
	}

	public void Cleanup(){
		if(this.db != null)
		{
			this.db.close();
			this.db = null;
		}
	}

	public void insert(Task task){
		ContentValues values = new ContentValues();
		values.put("description", task.getDescription());
		values.put("completed", task.isCompleted());
		values.put("notes", task.getNotes());
		values.put("location", task.getLocation());
		if(task.getTargetDate() != null) {
			values.put("targetdate",new SimpleDateFormat("yyyy-MM-dd HH:mm").format(task.getTargetDate()));
		}
		values.put("remindertype", task.getReminder());
		if(task.getReminderDate() != null) {
			values.put("reminderdate", new SimpleDateFormat("yyyy-MM-dd HH:mm").format(task.getReminderDate()));
		}

		task.setId(this.db.insert(DB_TASKS_TABLE, null, values));
	}

	public void update(Task task){
		ContentValues values = new ContentValues();
		values.put("description", task.getDescription());
		values.put("completed", task.isCompleted());
		values.put("snoozecount", task.getSnoozeCount());
		values.put("notes", task.getNotes());
		values.put("location", task.getLocation());
		if(task.getTargetDate() != null) {
			values.put("targetdate", new SimpleDateFormat("yyyy-MM-dd HH:mm").format(task.getTargetDate()));
		}
		else {
			values.put("targetdate", (String)null);
		}
		values.put("remindertype", task.getReminder());
		if(task.getReminderDate() != null) {
			values.put("reminderdate", new SimpleDateFormat("yyyy-MM-dd HH:mm").format(task.getReminderDate()));
		}
		else {
			values.put("reminderdate", (String)null);
		}


		this.db.update(DB_TASKS_TABLE, values, "id = " + task.getId(), null);
	}

	public void delete(int id){
		this.db.delete(DB_TASKS_TABLE, "id = " + id, null);
	}

	public List<Task> getActive(int pastWeeks, int futureWeeks) {
		return getTasks("completed = 0 AND (" +  formatDateRangeStatement(pastWeeks, futureWeeks) + 
				" OR targetdate is null)", null, null, null, "length(targetdate) DESC, strftime('%Y-%m-%d %H:%M', targetdate)");
	}

	public List<Task> getDue() {
		return getTasks("completed = 0 AND strftime('%Y-%m-%d %H:%M', targetdate) <= datetime('now', 'localtime')", 
				null, null, null, "strftime('%Y-%m-%d %H:%M', targetdate)");
	}

	public List<Task> getAll() {
		return getTasks(null, null, null, null, "length(targetdate) DESC, strftime('%Y-%m-%d %H:%M', targetdate)");
	}
	
	public List<Task> getAll(int pastWeeks, int futureWeeks){
		return getTasks(formatDateRangeStatement(pastWeeks, futureWeeks), null, null, null, 
				"length(targetdate) DESC, strftime('%Y-%m-%d %H:%M', targetdate)");
	}

	public List<Task> getTasks(String selection, String[] selectionArgs, String groupby, String having, String orderby){

		ArrayList<Task> list = new ArrayList<Task>();		
		Cursor c = null;

		try {
			c = this.db.query(DB_TASKS_TABLE, DBHelper.DB_TASKS_COLS, 
					selection, selectionArgs, groupby, having, orderby);

			int rowCount = c.getCount();

			c.moveToFirst();

			for(int counter = 0; counter < rowCount; counter++) {
				Task task = new Task();
				task.setId(c.getLong(0));
				task.setDescription(c.getString(1));
				task.setCompleted(c.getInt(2) == 0 ? false : true);

				if(c.isNull(3) == false) {
					String dateAsString = c.getString(3);
					if(dateAsString != null & dateAsString.length() > 0) {
						task.setTargetDate(new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(c.getString(3)));					
					}
				}

				task.setSnoozeCount(c.getInt(4));
				task.setNotes(c.getString(5));
				task.setLocation(c.getString(6));
				task.setReminder(c.getInt(7));
				if(c.isNull(8) == false) {
					String dateAsString = c.getString(8);
					if(dateAsString != null & dateAsString.length() > 0) {
						task.setReminderDate(new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(c.getString(8)));					
					}
				}				

				list.add(task);

				c.moveToNext();
			}
		}
		catch(SQLException e) {
			Log.e(Constants.LOGTAG, DBHelper.CLASSNAME, e);
		} catch (ParseException e) {
			Log.e(Constants.LOGTAG, DBHelper.CLASSNAME, e);
		}
		finally {
			if(c != null & c.isClosed() == false) {
				c.close();
			}			
		}

		return list;
	}

	private void establishDb()
	{
		if(this.db == null){
			this.db = dbOpenHelper.getWritableDatabase();
		}
	}

	public Task getSingle(long id) {
		List<Task> found = getTasks("id = " + id, null, null, null, null);
		return found.get(0);
	}

	public List<Task> getNoDate() {
		return getTasks("targetdate is null", null, null, null, null);
	}

	private String formatDateRangeStatement(int pastWeeks, int futureWeeks) {
		return "(targetdate between date('now', '-" + pastWeeks * 7 + " day') AND date('now', '+" + futureWeeks * 7 + " day'))";
	}
}
