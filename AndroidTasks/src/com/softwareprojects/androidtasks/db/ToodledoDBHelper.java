package com.softwareprojects.androidtasks.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.inject.Inject;
import com.softwareprojects.androidtasks.Constants;

public class ToodledoDBHelper {
	// DB names
	private static final String DB_NAME = "ToodledoTaskSync";
	private static final String DB_TASK_ID_MAPPINGS_TABLE = "TaskIdMappings";
	private static int DB_VERSION = 1;

	// Logging stuff
	private static final String CLASSNAME = ToodledoDBHelper.class.getSimpleName();

	// Instance of the database
	private SQLiteDatabase db;

	// Factory for database instance
	private DBOpenHelper dbOpenHelper; 

	private static class DBOpenHelper extends SQLiteOpenHelper
	{
		// SQL statement that creates the TASKS table 
		private static final String DB_CREATE_TASK_ID_MAPPINGS_TABLE = "CREATE TABLE " + 
		ToodledoDBHelper.DB_TASK_ID_MAPPINGS_TABLE + " (local_id INTEGER PRIMARY KEY, " + 
		"remote_id TEXT NOT NULL, modified INTEGER)";

		public DBOpenHelper(Context context) {
			super(context, ToodledoDBHelper.DB_NAME, null, ToodledoDBHelper.DB_VERSION);

		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			try{
				db.execSQL(DB_CREATE_TASK_ID_MAPPINGS_TABLE);
			}
			catch(SQLException e){
				Log.e(Constants.LOGTAG, ToodledoDBHelper.CLASSNAME, e);
			}
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + ToodledoDBHelper.DB_TASK_ID_MAPPINGS_TABLE);
			this.onCreate(db);
		}
	}

	@Inject
	public ToodledoDBHelper(Context context)
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

	private void establishDb()
	{
		if(this.db == null){
			this.db = dbOpenHelper.getWritableDatabase();
		}
	}

	public void insert(long local_id, String remote_id, long timestamp) {

		ContentValues values = new ContentValues();

		values.put("local_id", local_id);
		values.put("remote_id", remote_id);
		values.put("modified", timestamp);

		this.db.insert(DB_TASK_ID_MAPPINGS_TABLE, null, values);
	}

	public String findRemoteIdByLocalId(long local_id) {
		Cursor c = null;

		try {
			c = db.query(DB_TASK_ID_MAPPINGS_TABLE, new String[]{"local_id", "remote_id", "modified"}, 
					"local_id = ?", new String[]{String.valueOf(local_id)}, null, null, null);
			
			if(c.getCount() == 0) return null;
			c.moveToFirst();
			
			return c.getString(1);
		}
		catch(SQLException e) {
			Log.e(Constants.LOGTAG, ToodledoDBHelper.CLASSNAME, e);
		}
		finally {
			if(c != null & c.isClosed() == false) {
				c.close();
			}			
		}
		
		return null;
	}

	public long findLocalIdByRemoteId(String id) {
		Cursor c = null;

		try {
			c = db.query(DB_TASK_ID_MAPPINGS_TABLE, new String[]{"local_id", "remote_id", "modified"}, 
					"remote_id = ?", new String[]{id}, null, null, null);
			
			if(c.getCount() == 0) return 0;
			c.moveToFirst();
			
			return c.getLong(0);
		}
		catch(SQLException e) {
			Log.e(Constants.LOGTAG, ToodledoDBHelper.CLASSNAME, e);
		}
		finally {
			if(c != null & c.isClosed() == false) {
				c.close();
			}			
		}
		
		return -1;
	}

	public void deleteByLocalId(long id) {
		this.db.delete(DB_TASK_ID_MAPPINGS_TABLE, String.format("local_id = ?"), new String[]{String.valueOf(id)});
	}

	public void deleteByRemoteId(String id) {
		this.db.delete(DB_TASK_ID_MAPPINGS_TABLE, String.format("remote_id = ?"), new String[]{id});		
	}
}
