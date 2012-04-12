package hkust.comp3111h.focus.Adapter;

import hkust.comp3111h.focus.database.TaskItem;
import hkust.comp3111h.focus.database.TaskListItem;
import hkust.comp3111h.focus.database.TaskDbAdapter;
import hkust.comp3111h.focus.ui.*;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.util.Log;

//Current for testing
public final class TaskDnDAdapter extends BaseAdapter implements
    RemoveListener, DropListener {

  private Context mCtx;
  private int[] mIds;
  private int[] mLayouts;
  private LayoutInflater mInflater;
  private TaskDbAdapter mDbAdapter;
  private ArrayList<TaskItem> mTaskItems;

  public TaskDnDAdapter(Context context) {
    init(context, new int[] { android.R.layout.simple_list_item_1 },
        new int[] { android.R.id.text1 });
  }

  public TaskDnDAdapter(Context context, int[] itemLayouts, int[] itemIDs) {
    init(context, itemLayouts, itemIDs);
  }

  private void init(Context context, int[] layouts, int[] ids) {
    // Cache the LayoutInflate to avoid asking for a new one each time.
    mCtx = context;
    mInflater = LayoutInflater.from(context);
    mIds = ids;
    mLayouts = layouts;
    mDbAdapter = new TaskDbAdapter(mCtx);
    mDbAdapter.open();
    update();
  }

  /**
   * Fetch all tasks from database and refresh the tasks.
   */
  public void update() {
    mTaskItems = mDbAdapter.fetchAllTaskObjs(true);
  }

  /**
   * return Content saved inside the Adapter.
   * 
   * @return
   */

  public ArrayList<TaskItem> getContent() {
    return mTaskItems;
  }

  /**
   * The number of items in the list
   */
  public int getCount() {
    return mTaskItems.size();
  }

  /**
   * Since the data comes from an array, just returning the index is sufficient
   * to get at the data. If we were using a more complex data structure, we
   * would return whatever object represents one row in the list.
   * 
   * @see android.widget.ListAdapter#getItem(int)
   */
  public String getItem(int position) {
    return mTaskItems.get(position).taskName();
  }

  /**
   * Use the array index as a unique id.
   * 
   * @see android.widget.ListAdapter#getItemId(int)
   */
  public long getItemId(int position) {
    return position;
  }

  /**
   * Make a view to hold each row.
   * 
   */
  public View getView(int position, View convertView, ViewGroup parent) {
    // A ViewHolder keeps references to children views to avoid unneccessary
    // calls
    // to findViewById() on each row.
    ViewHolder holder;

    // When convertView is not null, we can reuse it directly, there is no need
    // to reinflate it. We only inflate a new View when the convertView supplied
    // by ListView is null.
    if (convertView == null) {
      convertView = mInflater.inflate(mLayouts[0], null);

      // Creates a ViewHolder and store references to the two children views
      // we want to bind data to.
      holder = new ViewHolder();
      holder.text = (TextView) convertView.findViewById(mIds[0]);

      convertView.setTag(holder);
    } else {
      // Get the ViewHolder back to get fast access to the TextView
      // and the ImageView.
      holder = (ViewHolder) convertView.getTag();
    }

    // Bind the data efficiently with the holder.
    holder.text.setText(mTaskItems.get(position).taskName());

    return convertView;
  }

  static class ViewHolder {
    TextView text;
  }

  public void onRemove(int which) {
    //waiting for hook to database
    /*
    if (which < 0 || which > mTaskItems.size())
      return;
    mTaskItems.remove(which);
    */
  }

  public void onDrop(int from, int to) {
    if(from == to) {
      return;
    }
    Log.d("DnDonDrop","From "+from + "To "+ to);
    mDbAdapter.updateTaskSequence(
        mTaskItems.get(from-1).taskId(),
        mTaskItems.get(to-1).taskId());
    update();
  }
}
