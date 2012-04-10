package hkust.comp3111h.focus.ui;

import hkust.comp3111h.focus.database.TaskListItem;
import hkust.comp3111h.focus.database.TaskItem;
import hkust.comp3111h.focus.Activity.MainActivity;

import android.app.Dialog;
import android.app.Activity;
import android.content.Context;
import android.view.WindowManager.LayoutParams;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.EditText;
import android.util.Log;

import hkust.comp3111h.focus.R;

public class QuickAddDialog extends Dialog {
  private ImageButton addButton;
  private Button cancelBtn;
  private Button detailBtn;
  private EditText taskNameEdit;
  private Activity mActivity;
  private TaskManageFragment mFragment;

  public QuickAddDialog(Context context, TaskManageFragment fragment) {
    super(context);
    mFragment = fragment;
    mActivity = fragment.getActivity();
    setContentView(R.layout.quick_add_dialog);
    setTitle("Quick Add Task");
    taskNameEdit = (EditText)findViewById(R.id.quickAddText);
    addButton = (ImageButton)findViewById(R.id.quick_add_button);
    cancelBtn = (Button)findViewById(R.id.cancel_button);
    //detailBtn = (Button)findViewById(R.id.detail_button);
    addButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        String tName;
        if((tName = taskNameEdit.getText().toString())!=null) {
          quickAddTask(tName);
        }
        dismiss();
        taskNameEdit.setText("");
        mFragment.updateList();
      }
    });
    cancelBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v){
        dismiss();
      }
    });
  }
  void quickAddTask(String tName) {
    Log.d("QuickAddDialog", "Adding Task");
    TaskListItem tlist = mFragment.getActiveTaskList();
    TaskItem newTask = new TaskItem();
    newTask.taskListId(tlist.taskListId());
    newTask.taskName(tName);
    newTask.taskType("Long Term");
    ((MainActivity)mActivity).getDbAdapter().createTask(newTask);
  }
}
