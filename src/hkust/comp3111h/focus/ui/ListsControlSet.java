package hkust.comp3111h.focus.ui;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import android.util.Log;

import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;

import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Button;
import android.view.View;

import hkust.comp3111h.focus.R;
import hkust.comp3111h.focus.database.TaskItem;
import hkust.comp3111h.focus.database.TaskDbAdapter;
import hkust.comp3111h.focus.database.TaskListItem;
import hkust.comp3111h.focus.util.PopupControlSet;
import hkust.comp3111h.focus.Activity.FocusBaseActivity;

/**
 * Control set for task list in EditTaskFragment
 * Probably use checkable list in the future, the API is provided for the future
 * extension. But currently, the list is uncheckable
 */
public final class ListsControlSet extends PopupControlSet {

  private ArrayList<TaskListItem> allLists;
  private ListView selectedLists;
  private TaskListItem currentList;
  HashMap<Long,TaskListItem> listIndices;
  private final TextView listsDisplay;
  private boolean populated = false;

  public ListsControlSet(Activity activity, int viewLayout, int displayViewLayout, int title) {
    super(activity, viewLayout, displayViewLayout, title);
    buildLists();
    listsDisplay = (TextView) getDisplayView().findViewById(R.id.display_row_edit);
    this.displayText.setText(activity.getString(R.string.TE_lists_label));
  }

  private void buildLists() {
    allLists = mDbAdapter.fetchAllTaskListsObjs(true);
  }

  /*
   * To fit the adapter that supports array only
   * Stupid
   */
  private TaskListItem[] getListArray() {
    return allLists.toArray(new TaskListItem[allLists.size()]);
  }
  

  private void setTagSelected() {
    //TODO
  }
  @Override
  public void initTask(TaskItem task) {
    super.initTask(task);
    Log.d("ListsControlSet", ""+task+ ""+mDbAdapter);
    if(task!=null&&task.taskListId()!=0) {
      currentList= mDbAdapter.fetchTaskListObj(task.taskListId());
    }else{
      currentList = mDbAdapter.fetchTaskListObj(1);
    }
    Log.d("LCS", "Current is"+currentList);
    populated = true;
  }


  @Override
  protected void initTask() {
    /*
    for(int i=0; i<selectedLists.getCount(); i++) {
      selectedLists.setItemChecked(i, true);
    }
    if(task!=null) {
      setSelectFromListItem();
    }
    */
    refreshDisplayView();
  }

  private void setSelectFromListItem() {
    //TODO
  }

  @Override
  protected void afterInflate() {
    selectedLists = (ListView) getView().findViewById(R.id.lists);
    selectedLists.setAdapter(new ArrayAdapter<TaskListItem>(activity, 
          R.layout.checked_list_row_layout, allLists));
    selectedLists.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
    selectedLists.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        currentList = (TaskListItem) parent.getAdapter().getItem(position);
        Log.d("ListsControlSet", "List Changed to"+currentList.taskListName());
        ((Button)getView().findViewById(R.id.edit_dlg_ok)).performClick();
      }
    });
  }

  @Override 
  public String writeDataAfterInit(TaskItem task) {
    Log.d("ListsControlSet", "Writing " + currentList.taskListName());
    task.taskListId(currentList.taskListId());
    return null;
  }

  @Override
  protected void refreshDisplayView() {
    if(currentList!=null) {
      listsDisplay.setText(currentList.taskListName());
    }  
  }
}
