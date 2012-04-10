package hkust.comp3111h.focus.Adapter;

import hkust.comp3111h.focus.R;
import hkust.comp3111h.focus.database.TaskDbAdapter;
import hkust.comp3111h.focus.database.TaskItem;
import android.content.Context;
import android.widget.TextView;
import java.util.ArrayList;

public class TaskWheelViewAdapter extends AbstractWheelTextAdapter {
  private ArrayList<TaskItem> mTasks;
  /**
   * Constructor 
   * @param contentext
   *          the current context
   */
  public TaskWheelViewAdapter(Context context,ArrayList<TaskItem> tasks) {
    super(context);
    mTasks = tasks;
  }
  @Override
  public CharSequence getItemText(int index) {
    if(index >= 0 && index < mTasks.size()) {
      return mTasks.get(index).taskName();
    }
    return null;
  }
  @Override 
  public int getItemsCount() {
    return mTasks.size();
  }
}
