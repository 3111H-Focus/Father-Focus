package hkust.comp3111h.focus.Activity;

import hkust.comp3111h.focus.R;
import hkust.comp3111h.focus.database.TaskDbAdapter;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

public class AddTaskActivity extends Activity implements OnClickListener{
	
	private EditText etTaskName;
	private EditText etTaskType;
	private EditText etTaskList;
	private Button bConfirm;
	private Button bCancel;
	private DatePicker dpDueDate;
	
	private TaskDbAdapter mDbHelper;
	private Long mRowId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mDbHelper = new TaskDbAdapter(this);
		mDbHelper.open();
		setContentView(R.layout.add_task);
		setTitle("Add Task");

		etTaskName = (EditText) findViewById(R.id.etTaskName);
		etTaskType = (EditText) findViewById(R.id.etTaskType);
		etTaskList = (EditText) findViewById(R.id.etTaskList);
		bConfirm = (Button) findViewById(R.id.bConfirm);
		bCancel = (Button) findViewById(R.id.bCancel);
		dpDueDate = (DatePicker) findViewById(R.id.dpDueDate);
		
		bConfirm.setOnClickListener(this);
		bCancel.setOnClickListener(this);

		populateFields();
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		saveState();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		populateFields();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		saveState();
		outState.putSerializable(TaskDbAdapter.KEY_TASK_TID, mRowId);
	}

	/**
	 * Fetch the task and fill the editText with the content. 
	 */
	private void populateFields() {
		if (mRowId != null) {
			Cursor mCursor = mDbHelper.fetchTask(mRowId);
			startManagingCursor(mCursor);
			etTaskName.setText(mCursor.getString(mCursor
					.getColumnIndexOrThrow(TaskDbAdapter.KEY_TASK_NAME)));
			etTaskType.setText(mCursor.getString(mCursor.getColumnIndexOrThrow(TaskDbAdapter.KEY_TASK_TYPE)));
			//TODO:DELETE THIS!! JUST FOR TEST!!!
			etTaskList.setText("3111H");
			//TODO:DATE SET FROM A STRING. 
		}
	}
	
	/**
	 * Get all info and update them in the database. 
	 */
	private void saveState() {
		String taskName = etTaskName.getText().toString();
		String taskType = etTaskType.getText().toString();
		//TODO:HARDCODED TASKLIST 
		//String taskList = etTaskList.getText().toString();
		String dueDate = this.dpDueDate.toString();
		
		if (mRowId == null){
			//TODO:DELETE the 1!! DEFAULT FOR 3111H. JUST FOR TEST. 
			long id = mDbHelper.createTask(1, taskType, taskName, dueDate, "", "");
			Log.d("id=", String.valueOf(id));
			if (id > 0){
				mRowId = id;
			} else {
				mDbHelper.updateTask(mRowId, taskType, taskName, dueDate, "", "");
			}
		}
	}

	/**
	 * Set result and return. You need to override the caller's function of
	 * onActivityResult. 
	 */
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.bConfirm:
			setResult(RESULT_OK);
			finish();
			break;
		case R.id.bCancel:
			//Create an intent and pass the result back to caller.
			//If the user click cancel, the caller should delete this row. 
			saveState(); //saveState to initialize mRowId
			Intent i = new Intent();
			i.putExtra(TaskDbAdapter.KEY_TASK_TID, mRowId);
			setResult(RESULT_CANCELED, i);
			finish();
			break;
		}
	}
}