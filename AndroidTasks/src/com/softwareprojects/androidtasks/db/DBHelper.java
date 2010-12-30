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
	private static int DB_VERSION = 1;

	private static final String[] DB_TASKS_COLS = 
		new String[]{"id", "description", "createdate", "completed", "targetdate", "snoozecount", "notes", 
		"location", "remindertype", "reminderdate", "recurrencetype", "recurrencevalue", "nextoccurrenceid"};

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
		"createdate TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP, " + 
		"completed INTEGER, targetdate TEXT, snoozecount INTEGER DEFAULT 0, notes TEXT, " + 
		"location TEXT, remindertype INTEGER DEFAULT 0, reminderdate TEXT, " + 
		"recurrencetype INTEGER DEFAULT 0, recurrencevalue INTEGER DEFAULT 0, " + 
		"nextoccurrenceid INTEGER DEFAULT 0);";

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
		values.put("remindertype", task.getReminderType());
		if(task.getReminderDate() != null) {
			values.put("reminderdate", new SimpleDateFormat("yyyy-MM-dd HH:mm").format(task.getReminderDate()));
		}
		values.put("recurrencetype", task.getRecurrenceType());
		values.put("recurrencevalue", task.getRecurrenceValue());
		values.put("nextoccurrenceid", task.getNextOccurrenceId());

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
		values.put("remindertype", task.getReminderType());
		if(task.getReminderDate() != null) {
			values.put("reminderdate", new SimpleDateFormat("yyyy-MM-dd HH:mm").format(task.getReminderDate()));
		}
		else {
			values.put("reminderdate", (String)null);
		}
		values.put("recurrencetype", task.getRecurrenceType());
		values.put("recurrencevalue", task.getRecurrenceValue());
		values.put("nextoccurrenceid", task.getNextOccurrenceId());


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
			if(rowCount == 0) return list;

			c.moveToFirst();

			for(int counter = 0; counter < rowCount; counter++) {
				Task task = new Task();
				task.setId(c.getLong(0));
				task.setDescription(c.getString(1));
				// Index 2 is skipped (create date)
				task.setCompleted(c.getInt(3) == 0 ? false : true);

				if(c.isNull(4) == false) {
					String dateAsString = c.getString(4);
					if(dateAsString != null & dateAsString.length() > 0) {
						task.setTargetDate(new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(c.getString(4)));					
					}
				}

				task.setSnoozeCount(c.getInt(5));
				task.setNotes(c.getString(6));
				task.setLocation(c.getString(7));
				task.setReminderType(c.getInt(8));
				if(c.isNull(9) == false) {
					String dateAsString = c.getString(9);
					if(dateAsString != null & dateAsString.length() > 0) {
						task.setReminderDate(new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(c.getString(9)));					
					}
				}
				task.setRecurrenceType(c.getInt(10));
				task.setRecurrenceValue(c.getInt(11));
				task.setNextOccurrenceId(c.getLong(12));

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
		if(found.size() == 0) return null;
		return found.get(0);
	}

	public void purge(int days) {
		String template = "%s < date('now', '-%d day')";
		db.execSQL("delete from Tasks where completed = 1 AND ((targetdate is not null AND " + 
				String.format(template, "targetdate", days) + ") OR (targetdate is null AND " + 
				String.format(template,"createdate", days) + "))");
	}

	public List<Task> getNoDate() {
		return getTasks("targetdate is null", null, null, null, null);
	}

	private String formatDateRangeStatement(int pastWeeks, int futureWeeks) {
		return "(targetdate between date('now', '-" + pastWeeks * 7 + " day') AND date('now', '+" + futureWeeks * 7 + " day'))";
	}
}
