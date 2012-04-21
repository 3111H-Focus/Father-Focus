package hkust.comp3111h.focus.ui;

import hkust.comp3111h.focus.database.TaskListItem;
import hkust.comp3111h.focus.database.TaskItem;
import hkust.comp3111h.focus.Activity.FocusBaseActivity;
import hkust.comp3111h.focus.Activity.MainActivity;

import android.app.Dialog;
import android.app.Activity;
import android.content.Context;
import android.view.WindowManager.LayoutParams;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.util.Log;

import hkust.comp3111h.focus.R;

public class AddTaskListDialog extends Dialog {
  private ImageButton addButton;
  private Button cancelBtn;
  private EditText tlistNameEdit;
  private Activity mActivity;
  private TaskManageFragment mFragment;
  public AddTaskListDialog(Context context, TaskManageFragment fragment) {
    super(context);
    mFragment = fragment;
    mActivity = fragment.getActivity();
    setContentView(R.layout.add_tlist_dialog);
    setTitle("Add New Task List");
    addButton = (ImageButton)findViewById(R.id.add_list_button);
    cancelBtn = (Button)findViewById(R.id.cancel_add_list_button);
    tlistNameEdit = (EditText)findViewById(R.id.addListEdit);
    addButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        String tlName;
        if((tlName = tlistNameEdit.getText().toString())!=null) {
          quickAddTaskList(tlName);
        }
        dismiss();
        tlistNameEdit.setText("");
        ((MainActivity)mActivity).updateData();
      }
    });
    cancelBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v){
        dismiss();
      }
    });
  }
  void quickAddTaskList(String tlName) {
    TaskListItem newTaskList = new TaskListItem();
    newTaskList.taskListName(tlName);
    long tListId = ((FocusBaseActivity)mActivity).getDbAdapter().createTaskList(tlName);
    newTaskList.taskListId(tListId);
    mFragment.setActiveTaskList(newTaskList);
  }
}

