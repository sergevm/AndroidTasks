package com.softwareprojects.androidtasks;

import java.util.List;

import com.softwareprojects.androidtasks.db.DBHelper;
import com.softwareprojects.androidtasks.domain.Task;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

public class TaskList extends ListActivity {

	private static DBHelper dbHelper;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        dbHelper = new DBHelper(this);
        
        List<Task> tasks = getTasks(); 
        
        ArrayAdapter<Task> adapter = 
        	new ArrayAdapter<Task>(this, R.layout.task_listitem, tasks);
        
        // Temporarily ... Will change this to a navigation ...
        this.getListView().setOnItemClickListener(
        	new AdapterView.OnItemClickListener(){

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					Task task = (Task)parent.getItemAtPosition(position);
					Toast.makeText(getBaseContext(), task.description, 
							Toast.LENGTH_SHORT).show();
				}
		});
        
        setListAdapter(adapter);
    }  
    
    
    
    private List<Task> getTasks()
    {
    	return dbHelper.getAll();
    }
}
