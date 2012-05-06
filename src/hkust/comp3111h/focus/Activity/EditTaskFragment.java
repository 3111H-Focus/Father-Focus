package hkust.comp3111h.focus.Activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import android.util.AttributeSet;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.SupportActivity;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import hkust.comp3111h.focus.R;
import hkust.comp3111h.focus.database.TaskDbAdapter;
import hkust.comp3111h.focus.database.TaskItem;
import hkust.comp3111h.focus.ui.TabPageIndicator;
import hkust.comp3111h.focus.ui.TaskTitleControlSet;
import hkust.comp3111h.focus.ui.DueDateControlSet;
import hkust.comp3111h.focus.ui.ListsControlSet;
import hkust.comp3111h.focus.util.TaskEditControlSet;
import hkust.comp3111h.focus.Activity.FocusBaseActivity;

public final class EditTaskFragment extends Fragment {
  private final int MENU_DISCARD_ID = R.string.TE_menu_discard;
  private final int MENU_SAVE_ID = R.string.TE_menu_save;
  private final int MENU_DELETE_ID=R.string.TE_menu_delete;
  public static final String TAG_EDITTASK_FRAGMENT = "edittask_fragment";


  private EditText title;
  private Dialog dueDialog;
  private ListsControlSet listsControlSet = null;
  private TaskDbAdapter mDbAdapter;


  /**
   * Task ID
   */
  public static final String TOKEN_ID = "id";
  /**
   * Content Values to set
   */
  public static final String TOKEN_VALUES = "v";

  private TaskItem taskInEdit;
  /**
   * Make use of polynophism, keep track on the sets 
   */
  private final List<TaskEditControlSet> controls = Collections.synchronizedList(new ArrayList<TaskEditControlSet>());

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getActivity().setResult(Activity.RESULT_OK);
  }
/*========================Setting up UI========================*/
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
    Log.d("EditTaskFragment", "creating view");
    super.onCreateView(inflater, container, savedInstanceState);
    View v = inflater.inflate(R.layout.task_edit_fragment_layout, container, false);
    return v;
  }

  @Override 
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    //To show menu items in ActionBar
    setHasOptionsMenu(true);

    //Get the editing task
    long taskId = getActivity().getIntent().getLongExtra(TOKEN_ID,0);
    Log.d("TaskEditFrag","taskId ="+taskId);
    mDbAdapter = ((FocusBaseActivity)getActivity()).getDbAdapter();
    if(taskId!=0) {
       taskInEdit = mDbAdapter.fetchTaskObj(taskId);
    }else{
      taskInEdit = new TaskItem();
    }
    initUIComponents();
  }

  /** Initialize ui components */
  private void initUIComponents() {
    //Data initialization
    LinearLayout basicControls = (LinearLayout) getView().findViewById(
        R.id.basic_controls);
    LinearLayout titleControls = (LinearLayout) getView().findViewById(
        R.id.title_controls);
    /*
    LinearLayout dueDateDialogView = (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.due_date_dialog,null);
    constructDueDateDialog(dueDateDialogView);
    */
    //Title controlset initalization
    TaskTitleControlSet editTitle = new TaskTitleControlSet(getActivity(),
        R.layout.task_edit_title_layout, R.id.title);
    title = (EditText) editTitle.getView().findViewById(R.id.title);
    controls.add(editTitle);
    titleControls.addView(editTitle.getDisplayView(), 0, 
        new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 1.0f));
    //Due date controlset initialization
    DueDateControlSet dueDateControl = new DueDateControlSet(
        getActivity(), R.layout.due_date_control_layout,
        R.layout.control_set_base_layout);
    controls.add(dueDateControl);
    basicControls.addView(dueDateControl.getDisplayView());
    //Lists control
    listsControlSet = new ListsControlSet(
        getActivity(), R.layout.edit_control_lists_popover,
        R.layout.control_set_base_layout, R.string.TE_lists_label_long);
    controls.add(listsControlSet);
    basicControls.addView(listsControlSet.getDisplayView());
    populateField();
  }

  private void populateField() {
    for(TaskEditControlSet controlSet: controls) {
      controlSet.initTask(taskInEdit);
    }
  }


  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
    Log.d("TaskEditFragment", "Creating options menu");
    MenuItem item;
    item = menu.add(Menu.NONE,MENU_DISCARD_ID , 0, R.string.TE_menu_discard);
    item.setIcon(android.R.drawable.ic_menu_close_clear_cancel);
    item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

    item = menu.add(Menu.NONE, MENU_SAVE_ID, 0, R.string.TE_menu_save);
    item.setIcon(android.R.drawable.ic_menu_save);
    item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

    item = menu.add(Menu.NONE, MENU_DELETE_ID, 0, R.string.TE_menu_delete);
    item.setIcon(android.R.drawable.ic_menu_delete);
    item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
  }

  public static void setViewHeightBasedOnChild(LinearLayout view) {
    int totalHeight = 0;
    int desiredWidth = MeasureSpec.makeMeasureSpec(view.getWidth(),
            MeasureSpec.AT_MOST);
    for(int i=0; i < view.getChildCount(); i++) {
      View listItem = view.getChildAt(i);
      listItem.measure(desiredWidth, MeasureSpec.UNSPECIFIED);
      totalHeight+=listItem.getMeasuredHeight();
    }
    ViewGroup.LayoutParams params = view.getLayoutParams();
    if(params == null) {
      return;
    }
    params.height = totalHeight;
    view.setLayoutParams(params);
    view.requestLayout();
  }
  @Override 
  public void onResume() {
    super.onResume();
    populateField();
  }
  /* ==========================================================
   * ========== event handling ===============================
   */
  private boolean writeControlSetData() {
    if(title==null) {
      return false;
    }
    if(title.getText().toString().length()==0) {
      return false;
    }
    if(title.getText().toString().length()>0) {
      taskInEdit.taskName(title.getText().toString());
    }
    synchronized(controls) {
      for(TaskEditControlSet controlSet: controls){
        controlSet.writeData(taskInEdit);
      }
    }
    return true;
  }
  
  protected void saveButtonClick() {
    //ToDo
    Log.d("EditTaskFragment", "Save!");
    //for the exisiting task, update
    //If the title is empty sorry
    boolean isValid = writeControlSetData();
    if(!isValid) {
      discardButtonClick();
      return;
    }else if(taskInEdit.taskId()!=0){
      Log.d("EditTaskFragment", "Updating Exist "+ taskInEdit.taskName() + " " + taskInEdit.dueDate());
      mDbAdapter.updateTask(
          taskInEdit.taskId(),
          taskInEdit.taskType(),
          taskInEdit.taskName(),
          taskInEdit.status(),
          taskInEdit.dueDate(),
          taskInEdit.taskListId());
    }else { //for the new task, add it
      Log.d("EditTaskFragment", "Creating new");
      taskInEdit.taskType("Long Term");
      if(taskInEdit.taskListId()==0){
        taskInEdit.taskListId(1);
      }
      long id = mDbAdapter.createTask(taskInEdit);
      Log.v("EditTaskFragment", "New Task Id"+id);
    }
    discardButtonClick();
  }

  protected void discardButtonClick() {
    getActivity().onBackPressed();
  }

  protected void deleteButtonClick() {
    Log.d("EditTaskFragment", "Delete");
    if(taskInEdit.taskId()!=0) {
      mDbAdapter.deleteTask(taskInEdit.taskId());
    }
    discardButtonClick();
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch(item.getItemId()) {
      case MENU_SAVE_ID:
        saveButtonClick();
        return true;
      case MENU_DELETE_ID:
        deleteButtonClick();
        return true;
      case MENU_DISCARD_ID:
        discardButtonClick();
        return true;
      case android.R.id.home:
        if(title.getText().toString().length() == 0)
          discardButtonClick();
        else
          saveButtonClick();
        return true;
    }

    return super.onOptionsItemSelected(item);
  }
}

