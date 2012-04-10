package hkust.comp3111h.focus.Adapter;

import android.content.Context;
import java.util.ArrayList;
import android.widget.TextView;
import hkust.comp3111h.focus.database.TaskListItem;

public class TaskListWheelAdapter extends AbstractWheelTextAdapter {
  private ArrayList<TaskListItem> mTaskLists;
  /**
   * Constructor
   * @param context
   * @param taskLists
   */
  public TaskListWheelAdapter(Context context, ArrayList<TaskListItem> taskLists) {
    super(context);
    mTaskLists = taskLists;
  }
  @Override
  public CharSequence getItemText(int index) {
    if(index >= 0 && index < mTaskLists.size()) {
      return mTaskLists.get(index).taskListName();
    }
    return null;
  }
  @Override
  public int getItemsCount() {
    return mTaskLists.size();
  }
}
