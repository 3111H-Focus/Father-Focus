package hkust.comp3111h.focus.Adapter;

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

//Current for testing
public final class TaskDnDAdapter extends BaseAdapter implements
    RemoveListener, DropListener {

  private Context mCtx;
  private int[] mIds;
  private int[] mLayouts;
  private LayoutInflater mInflater;
  private TaskDbAdapter mDbAdapter;
  private ArrayList<String> mContent;

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
    mContent = new ArrayList<String>();
    mDbAdapter = new TaskDbAdapter(mCtx);
    mDbAdapter.open();

    long tl1 = mDbAdapter.createTaskList("3111H");
    long tl2 = mDbAdapter.createTaskList("2031");

    /*
     * mDbAdapter.createTask(tl1, "Project", "Database checking", "Next week",
     * "Today", "Tomorrow"); mDbAdapter.createTask(tl1, "Assignment",
     * "UML Diagram", "Next Monday", "Tomorrow", "TBD");
     * mDbAdapter.createTask(tl1, "Coding", "Part-time job", "Today", "TBD",
     * "TBD");
     * 
     * mDbAdapter.createTask(tl2, "Presentation", "Exercise11", "Tuesday",
     * "Thursday", ""); mDbAdapter.createTask(tl2, "Writing", "Homework",
     * "Wednesday", "", "");
     */
  }

  /**
   * Fetch all tasks from database and refresh the tasks.
   */
  public void update() {
    mContent.clear();
    // Insertion of dummy records.

    Cursor mCursor = mDbAdapter.fetchAllTasks();
    for (mCursor.moveToFirst(); !mCursor.isAfterLast(); mCursor.moveToNext()) {
      mContent.add(mCursor.getString(mCursor
          .getColumnIndex(TaskDbAdapter.KEY_TASK_NAME)));
    }

    mCursor.close();
  }

  /**
   * return Content saved inside the Adapter.
   * 
   * @return
   */

  public ArrayList<String> getContent() {
    return mContent;
  }

  /**
   * The number of items in the list
   */
  public int getCount() {
    return mContent.size();
  }

  /**
   * Since the data comes from an array, just returning the index is sufficient
   * to get at the data. If we were using a more complex data structure, we
   * would return whatever object represents one row in the list.
   * 
   * @see android.widget.ListAdapter#getItem(int)
   */
  public String getItem(int position) {
    return mContent.get(position);
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
   * @see android.widget.ListAdapter#getView(int, android.view.View,
   *      android.view.ViewGroup)
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
    holder.text.setText(mContent.get(position));

    return convertView;
  }

  static class ViewHolder {
    TextView text;
  }

  public void onRemove(int which) {
    if (which < 0 || which > mContent.size())
      return;
    mContent.remove(which);
  }

  public void onDrop(int from, int to) {
    String temp = mContent.get(from);
    mContent.remove(from);
    mContent.add(to, temp);
  }
}
