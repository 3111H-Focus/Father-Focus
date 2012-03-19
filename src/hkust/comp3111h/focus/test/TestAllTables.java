package hkust.comp3111h.focus.test;

import hkust.comp3111h.focus.R;
import hkust.comp3111h.focus.database.TaskDbAdapter;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class TestAllTables extends Activity implements OnClickListener{
	Button bInsert;
	TextView tvTestAllTables;
	
	private TaskDbAdapter mTaskDbAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test_all_tables);
		bInsert = (Button) findViewById(R.id.bAddData);
		bInsert.setOnClickListener(this);
		
		mTaskDbAdapter = new TaskDbAdapter(this);
		mTaskDbAdapter.open();
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.bAddData:
			addData();
			break;
		}
	}
	
	private void addData(){
		mTaskDbAdapter.createUser("Kevin Chen", "I'm a database designer!");
		mTaskDbAdapter.createUser("Yiufung Cheong", "I'm a password");
		mTaskDbAdapter.createUser("Gary Cheung", "Lock you my window!");
		mTaskDbAdapter.createUser("Gary Lee", "Ahhh this looks ugly!!");
		
		long tlid = mTaskDbAdapter.createTaskList("COMP3111H Project");
		
		mTaskDbAdapter.createTask(tlid, "Project", "write code", "next monday", "NOW!!!", "who knows");
		mTaskDbAdapter.createTask(tlid, "Assignment", "exercises on P90.", "monday", "hea hea..", "none");
		mTaskDbAdapter.createTask(tlid, "Love", "Go out with GF", "tonight", "NOW!!!", "none");
	}
	
	

}
