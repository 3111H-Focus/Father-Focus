package hkust.comp3111h.focus.test;

import hkust.comp3111h.focus.R;
import hkust.comp3111h.focus.database.TaskDbAdapter;

import java.util.ArrayList;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class DBTestDragAndDrop extends Activity implements OnClickListener {
  private EditText etDrag;
  private EditText etDrop;
  private ListView lvId;
  private ListView lvSeq;
  private Button bDrop;

  private TaskDbAdapter mDbAdapter;
  private ArrayList<Long> ids;
  private ArrayList<Long> seqs;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    // TODO Auto-generated method stub
    super.onCreate(savedInstanceState);
    setContentView(R.layout.db_test_draganddrop);
    etDrag = (EditText) findViewById(R.id.etDrag);
    etDrop = (EditText) findViewById(R.id.etDrop);
    lvId = (ListView) findViewById(R.id.lvId);
    lvSeq = (ListView) findViewById(R.id.lvSeq);
    bDrop = (Button) findViewById(R.id.bDrop);

    bDrop.setOnClickListener(this);
    
    mDbAdapter = new TaskDbAdapter(this);
    mDbAdapter.open();
    //Uncomment this at first run to add data. 
    addData();
    
    ids = new ArrayList<Long>(); 
    seqs = new ArrayList<Long>();
    
    refresh();
  }

  private void addData() {
    mDbAdapter.createTaskList("a");
    mDbAdapter.createTaskList("b");
    mDbAdapter.createTaskList("c");
    mDbAdapter.createTaskList("d");
    mDbAdapter.createTaskList("e");
    mDbAdapter.createTaskList("f");
  }
  
  private void testDragAndDrop() {
    long drag = Long.parseLong(etDrag.getText().toString());
    long drop = Long.parseLong(etDrop.getText().toString());
    mDbAdapter.updateTaskListSequence(drag, drop);
    refresh();
  }

  // Fetch data and refresh the list.
  private void refresh() {
    ids.clear();
    seqs.clear();
    
    Cursor mCursor = mDbAdapter.fetchAllTaskLists(true);
    for (mCursor.moveToFirst(); !mCursor.isAfterLast(); mCursor.moveToNext()) {
      ids.add(mCursor.getLong(mCursor
          .getColumnIndexOrThrow(TaskDbAdapter.KEY_TASKLIST_TLID)));
      seqs.add(mCursor.getLong(mCursor
          .getColumnIndexOrThrow(TaskDbAdapter.KEY_TASKLIST_TLSEQUENCE)));
    }
    
    ArrayAdapter<Long> idAdapter = new ArrayAdapter<Long>(this, android.R.layout.simple_list_item_1, android.R.id.text1, ids);
    ArrayAdapter<Long> seqAdapter = new ArrayAdapter<Long>(this, android.R.layout.simple_list_item_1, android.R.id.text1, seqs);
    lvId.setAdapter(idAdapter);
    lvSeq.setAdapter(seqAdapter);
  }


  @Override
  public void onClick(View v) {
    // TODO Auto-generated method stub
    switch (v.getId()) {
      case R.id.bDrop:
        testDragAndDrop();
        break;
    }
  }
}
