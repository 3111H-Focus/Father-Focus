package hkust.comp3111h.focus.test;

import hkust.comp3111h.focus.R;
import hkust.comp3111h.focus.database.TaskDbAdapter;
import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;


public class TaskDbAdapterTest extends Activity implements OnClickListener{
	
	EditText etStatus;
	Button bAddData;
	Button bTC1;
	Button bTC2;
	Button bTC3;
	Button bTC4;
	Button bTC5;
	Button bTC6;
	
	long tl1;
	long tl2;
	long task1;
	long task2;
	long task3;
	long task4;
	long task5;
	
	TaskDbAdapter mDbAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.testalltables);
		
		etStatus = (EditText) findViewById(R.id.etStatus);
		bAddData = (Button) findViewById(R.id.bAddData);
		bTC1 = (Button) findViewById(R.id.bTestCase1);
		bTC2 = (Button) findViewById(R.id.bTestCase2);
		bTC3 = (Button) findViewById(R.id.bTestCase3);
		bTC4 = (Button) findViewById(R.id.bTestCase4);
		bTC5 = (Button) findViewById(R.id.bTestCase5);
		bTC6 = (Button) findViewById(R.id.bTestCase6);
		bAddData.setOnClickListener(this);
		bTC1.setOnClickListener(this);
		bTC2.setOnClickListener(this);
		bTC3.setOnClickListener(this);
		bTC4.setOnClickListener(this);
		bTC5.setOnClickListener(this);
		bTC6.setOnClickListener(this);
		
		mDbAdapter = new TaskDbAdapter(this);
		mDbAdapter.open();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.bAddData:
			addData();
			break;
		case R.id.bTestCase1:
			TestCase1();
			break;
		case R.id.bTestCase2:
			TestCase2();
			break;
		case R.id.bTestCase3:
			TestCase3();
			break;
		case R.id.bTestCase4:
			TestCase4();
			break;
		case R.id.bTestCase5:
			TestCase5();
			break;
		case R.id.bTestCase6:
			TestCase6();
			break;
		}
	}
	
	public void addData(){
		etStatus.setText("Insert data into database. ");
		
		tl1= mDbAdapter.createTaskList("3111H");
		tl2= mDbAdapter.createTaskList("2031");
		
		task1 = mDbAdapter.createTask(tl1, "Project", "Database checking", "Next week", "Today", "Tomorrow");
		task2 = mDbAdapter.createTask(tl1, "Assignment", "UML Diagram", "Next Monday", "Tomorrow", "TBD");
		task3 = mDbAdapter.createTask(tl1, "Coding", "Part-time job", "Today", "TBD", "TBD");
		
		task4 = mDbAdapter.createTask(tl2, "Presentation", "Exercise11", "Tuesday", "Thursday", "");
		task5 = mDbAdapter.createTask(tl2, "Writing", "Homework", "Wednesday", "", "");
		
		etStatus.setText(etStatus.getText() + "Data added. use adb to see content.");
	}
	
	public void TestCase1(){
		etStatus.setText("Testcase 1: fetch one taskList from 3111H. ");
		
		Cursor mCursor = mDbAdapter.fetchTaskList(tl1);
		startManagingCursor(mCursor);
		mCursor.moveToFirst();
		etStatus.setText(etStatus.getText() + "Info of task list with id 1 should be 3111H. Result: " +
				"mCursor count: " + mCursor.getCount() + " mCursor value: " + 
				mCursor.getString(mCursor.getColumnIndex(TaskDbAdapter.KEY_TASKLIST_TLNAME)));
	}
	
	public void TestCase2(){
		etStatus.setText("Testcase 2: fetch all tasklists info from 3111H. ");
		Cursor mCursor = mDbAdapter.fetchAllTaskLists();
		startManagingCursor(mCursor);
		mCursor.moveToFirst();
		etStatus.setText(etStatus.getText() + "Should be 3111H + 2031. Result: " +
			mCursor.getString(mCursor.getColumnIndex(TaskDbAdapter.KEY_TASKLIST_TLNAME)));
		mCursor.moveToNext();
		etStatus.setText(etStatus.getText() + " " +
			mCursor.getString(mCursor.getColumnIndex(TaskDbAdapter.KEY_TASKLIST_TLNAME)));
	}
	
	public void TestCase3(){
		etStatus.setText("Testcase 3: delete a task(id=3) in 3111H taskList(id=1). ");
		mDbAdapter.deleteTask(tl1, task3);
		etStatus.setText(etStatus.getText() + "Check task table. Should be only 4 tasks" +
				" left, 2 with tlid=1 and 2 with tlid=2. ");
	}
	
	public void TestCase4(){
		etStatus.setText("Testcase 4: delete tasklist 3111H(tlid=1). ");
		
		mDbAdapter.deleteTaskList(tl1);
		
		etStatus.setText(etStatus.getText()+"On cascade deleting, so TaskList3111H" +
				"should no longer exists and tasks with id = 1 should be deleted as well. ");
	}
	
	public void TestCase5(){
		etStatus.setText("Testcase 5: update 2031 tasklist. Change name to Analysis. ");
		
		mDbAdapter.updateTaskList(tl2, "Analysis");
		
		Cursor mCursor = mDbAdapter.fetchTaskList(tl2);
		startManagingCursor(mCursor);
		mCursor.moveToFirst();
		
		etStatus.setText(etStatus.getText()+"Result: " +
				mCursor.getString(mCursor.getColumnIndex(TaskDbAdapter.KEY_TASKLIST_TLNAME)));
		
	}
	
	public void TestCase6(){
		
	}
}
