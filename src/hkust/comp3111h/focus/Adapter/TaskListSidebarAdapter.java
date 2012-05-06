package hkust.comp3111h.focus.Adapter;

import hkust.comp3111h.focus.database.TaskItem;
import hkust.comp3111h.focus.database.TaskListItem;
import hkust.comp3111h.focus.R;


import java.util.List;
import java.util.ArrayList;
import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.DisplayMetrics;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.content.Context;


public class TaskListSidebarAdapter extends ArrayAdapter<TaskListItem> {
  /** parent activity */
  protected final Activity activity;

  /** owner listview */
  protected final ListView listView;

  /** row layout to inflat */
  private final int layout;

  /** layout inflater */
  private final LayoutInflater inflater;

  public TaskListSidebarAdapter(
      Activity activity, 
      ListView listView,
      ArrayList<TaskListItem> items,
      int rowLayout) {
    super(activity,0,items);
    this.activity = activity;
    this.listView = listView;
    this.layout = rowLayout;
    inflater = (LayoutInflater) activity.getSystemService(
              Context.LAYOUT_INFLATER_SERVICE);
  }

  @Override 
  public View getView(int position, View convertView, ViewGroup parent) {
    convertView  = newView(convertView,parent);
    ViewHolder viewHolder = (ViewHolder) convertView.getTag();
    //viewHolder.icon.setImageResource(R.drawable.list_icon);
    if(position == 0){
      viewHolder.item = null;
      viewHolder.name.setText("New List");
      viewHolder.name.setCompoundDrawablesWithIntrinsicBounds(R.drawable.plus_button_black,0,0,0);
      viewHolder.name.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
    } else if(position == 1) {
      viewHolder.name.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
      viewHolder.item = null;
      viewHolder.name.setText("All Tasks");
    } else{
      viewHolder.item = (TaskListItem) getItem(position-2);
      viewHolder.name.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
      viewHolder.name.setText(viewHolder.item.taskListName());
    }
    return convertView;
  }

  @Override
  public int getCount() {
    return super.getCount()+2;
  }

  public void populateView(ViewHolder viewHolder) {
    TaskListItem item = viewHolder.item;
    
  }
  public static class ViewHolder {
    public TaskListItem item;
    //public ImageView icon;
    public TextView name;
    public View view;
  }

  /**
   * Create or reuse a view
   */
  protected View newView(View convertView, ViewGroup parent) {
    if(convertView == null) {
      convertView = inflater.inflate(layout,parent,false);
      ViewHolder viewHolder = new ViewHolder();
      viewHolder.view = convertView;
      //viewHolder.icon = (ImageView) convertView.findViewById(R.id.icon);
      viewHolder.name = (TextView) convertView.findViewById(R.id.name);
      convertView.setTag(viewHolder);
    }
    return convertView;
  }
}
