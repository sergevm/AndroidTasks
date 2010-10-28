package com.softwareprojects.androidtasks.db;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.softwareprojects.androidtasks.Constants;
import com.softwareprojects.androidtasks.domain.Task;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper {
	// DB names
	private static final String DB_NAME = "AndroidTasks";
	private static final String DB_TASKS_TABLE = "Tasks";
	private static int DB_VERSION = 1;
	
	private static final String[] DB_TASKS_COLS = 
		new String[]{"id", "description", "completed", "targetdate"};
	
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
			"completed INTEGER, targetdate TEXT);";
		
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
			db.execSQL("DELETE TABLE IF EXISTS " + DBHelper.DB_TASKS_TABLE);
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
		values.put("targetdate",DateFormat.getDateTimeInstance().format(task.targetDate));

		 task.id = this.db.insert(DB_TASKS_TABLE, null, values);
	}
	
	public void update(Task task){
		ContentValues values = new ContentValues();
		values.put("description", task.description);
		values.put("completed", task.completed);
		values.put("targetdate", DateFormat.getDateTimeInstance().format(task.targetDate));
		
		this.db.update(DB_TASKS_TABLE, values, "id = " + task.id, null);
	}
	
	public void delete(int id){
		this.db.delete(DB_TASKS_TABLE, "id = " + id, null);
	}
	
	public List<Task> getAll(){
		ArrayList<Task> list = new ArrayList<Task>();
		
		Cursor c = null;
		
		try {
			c = this.db.query(DB_TASKS_TABLE, DBHelper.DB_TASKS_COLS, 
					null, null, null, null, null);
			
			int rowCount = c.getCount();
			
			c.moveToFirst();
			
			for(int counter = 0; counter < rowCount; counter++) {
				Task task = new Task();
				task.id = c.getLong(0);
				task.description = c.getString(1);
				task.completed = Boolean.parseBoolean(c.getString(2));
				
				if(c.isNull(3) == false) {
					String dateAsString = c.getString(3);
					if(dateAsString != null & dateAsString.length() > 0) {
						task.targetDate = new SimpleDateFormat("dd-MM-yyyy").parse(c.getString(3));					
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
}
