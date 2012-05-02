package hkust.comp3111h.focus.ui;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import hkust.comp3111h.focus.R;
import hkust.comp3111h.focus.database.TaskItem;
import hkust.comp3111h.focus.util.TaskEditControlSet;

public class TaskTitleControlSet extends TaskEditControlSet {
  private EditText editText;
 private final int editTextId;
 
 public TaskTitleControlSet(Activity activity, int layout, int editText) {
   super(activity, layout);
   this.editTextId = editText;
 }
 @Override
 protected void afterInflate() {
   this.editText = (EditText) getView().findViewById(editTextId);
 }
 @Override
 protected void initTask(){
   editText.setTextKeepState(task.taskName());
 }
 @Override
 protected String writeDataAfterInit(TaskItem task) {
   task.taskName(editText.getText().toString());
   return null;
 }
}
