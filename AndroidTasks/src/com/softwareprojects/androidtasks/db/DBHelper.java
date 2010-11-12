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
	private static int DB_VERSION = 2;

	private static final String[] DB_TASKS_COLS = 
		new String[]{"id", "description", "completed", "targetdate", "snoozecount", "notes", "location"};

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
		"completed INTEGER, targetdate TEXT, snoozecount INTEGER DEFAULT 0, notes TEXT, location TEXT);";

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
		values.put("description", task.description);
		values.put("completed", task.completed);
		values.put("notes", task.notes);
		values.put("location", task.location);
		if(task.targetDate != null) {
			values.put("targetdate",new SimpleDateFormat("yyyy-MM-dd HH:mm").format(task.targetDate));
		}

		task.id = this.db.insert(DB_TASKS_TABLE, null, values);
	}

	public void update(Task task){
		ContentValues values = new ContentValues();
		values.put("description", task.description);
		values.put("completed", task.completed);
		values.put("snoozecount", task.snoozeCount);
		values.put("notes", task.notes);
		values.put("location", task.location);
		if(task.targetDate != null) {
			values.put("targetdate", new SimpleDateFormat("yyyy-MM-dd HH:mm").format(task.targetDate));
		}
		else {
			values.put("targetdate", (String)null);
		}

		this.db.update(DB_TASKS_TABLE, values, "id = " + task.id, null);
	}

	public void delete(int id){
		this.db.delete(DB_TASKS_TABLE, "id = " + id, null);
	}

	public List<Task> getActive() {
		return getTasks("completed = 0", null, null, null, "targetdate");
	}

	public List<Task> getDue() {
		return getTasks("completed = 0 AND strftime('%Y-%m-%d %H:%M', targetdate) <= datetime('now', 'localtime')", null, null, null, "targetdate");
	}

	public List<Task> getAll(){
		return getTasks(null, null, null, null, null);
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
				task.id = c.getLong(0);
				task.description = c.getString(1);
				task.completed = c.getInt(2) == 0 ? false : true;

				if(c.isNull(3) == false) {
					String dateAsString = c.getString(3);
					if(dateAsString != null & dateAsString.length() > 0) {
						task.targetDate = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(c.getString(3));					
					}
				}

				task.snoozeCount = c.getInt(4);
				task.notes = c.getString(5);
				task.location = c.getString(6);

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
}
