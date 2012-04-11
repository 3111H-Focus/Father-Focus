package hkust.comp3111h.focus.Activity;

import hkust.comp3111h.focus.R;
import hkust.comp3111h.focus.database.TaskDbAdapter;

import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

public class AddTaskActivity extends Activity implements OnClickListener {

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

    // mRowId will be initialized in here.
    saveState();
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
  private void populateFields() throws CursorIndexOutOfBoundsException {
    try {
      if (mRowId != null) {
        Cursor mCursor = mDbHelper.fetchTask(mRowId);
        startManagingCursor(mCursor);
        mCursor.moveToFirst();
        etTaskName.setText(mCursor.getString(mCursor
            .getColumnIndexOrThrow(TaskDbAdapter.KEY_TASK_NAME)));
        etTaskType.setText(mCursor.getString(mCursor
            .getColumnIndexOrThrow(TaskDbAdapter.KEY_TASK_TYPE)));
        // TODO:DELETE THIS!! JUST FOR TEST!!!
        etTaskList.setText("3111H");
        // TODO:DATE SET FROM A STRING.
      }
    }
    catch (CursorIndexOutOfBoundsException e) {
      // Definitely not right to do this.
      // This happens when you go in and immediately click cancel.
      etTaskName.setText("");
      etTaskType.setText("");
      etTaskList.setText("3111H");
    }
  }

  /**
   * Get all info and update them in the database.
   */
  private void saveState() {
    String taskName = etTaskName.getText().toString();
    String taskType = etTaskType.getText().toString();
    // TODO:HARDCODED TASKLIST
     String taskList = etTaskList.getText().toString();
    Date dpDate = new Date(dpDueDate.getYear() - 1900, dpDueDate.getMonth(),
        dpDueDate.getDayOfMonth());
    String dueDate = dpDate.toGMTString();

    if (mRowId == null) {
      // TODO:DELETE the 1!! DEFAULT FOR 3111H. JUST FOR TEST.
      long id = mDbHelper.createTask(1, taskType, taskName, TaskDbAdapter.STATUS_NOT_START, dueDate);
      Log.d("id=", String.valueOf(id));
      if (id > 0) {
        mRowId = id;
      } else {
        Log.d("AddTaskActivity",mRowId.toString()+ taskType.toString()+ taskName.toString()+dueDate.toString());
        mDbHelper.updateTask(mRowId, taskType, taskName, TaskDbAdapter.STATUS_NOT_START, dueDate);
      }
    } else {
      mDbHelper.updateTask(mRowId, taskType, taskName, TaskDbAdapter.STATUS_NOT_START, dueDate);
    }
  }

  /**
   * Set result and return. You need to override the caller's function of
   * onActivityResult.
   */
  @Override
  public void onClick(View v) {
    // TODO Auto-generated method stub
    switch (v.getId()) {
      case R.id.bConfirm:
        if (etTaskName.getText().toString().length() == 0) {
          etTaskName.setHint("Please specify Task Name.");
          break;
        } else {
          setResult(RESULT_OK);
          finish();
          break;
        }
      case R.id.bCancel:
        // Create an intent and pass the result back to caller.
        // If the user click cancel, the caller should delete this row.
        Intent i = new Intent();
        Log.d("In cancel, mRowId=", String.valueOf(mRowId));
        i.putExtra(TaskDbAdapter.KEY_TASK_TID, mRowId);
        setResult(RESULT_CANCELED, i);
        finish();
        break;
    }
  }
}
