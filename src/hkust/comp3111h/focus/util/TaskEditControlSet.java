package hkust.comp3111h.focus.util;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;

import hkust.comp3111h.focus.database.TaskItem;
/**
 * The base class for all the control set class 
 * In task Edit Fragment
 * Basically act as a interface
 */
public abstract class TaskEditControlSet {
  protected final Activity activity;
  private final int viewLayout;
  private View view;
  protected TaskItem task;
  protected boolean initialized = false;

  /**
   * @param activity
   * @param viewLayout
   */
  public TaskEditControlSet(Activity activity, int viewLayout) {
    this.activity = activity;
    this.viewLayout = viewLayout;
    if(viewLayout == -1) 
      initialized = true;
  }

  /**
   * get the root view of a control set
   */
  public View getView() {
    if(view == null && !initialized) {
      if(viewLayout!= -1) {
        view = LayoutInflater.from(activity).inflate(viewLayout,null);
        afterInflate(); 
      }
      if(task!=null) {
        initTask();
      }
      this.initialized = true;
    }
    return view;
  }
  /**
   * Get the view for display, the interface here is basically 
   * the getView funtion, just to differentiate their funcitons
   * getView is for reference to the view and getDisplayView
   * is for display
   */

  public View getDisplayView() {
    return getView();
  }
  public void initTask(TaskItem task) {
    this.task = task;
    if(initialized) {
      initTask();
    }
  }
  /**
   * Call once to setup the ui with data from the TaskItem
   */
  protected abstract void initTask();

  /**
   * Write data from control set to the task item
   * @return text appended to the toast
   */
  public String writeData(TaskItem task) {
    if(initialized) {
      return writeDataAfterInit(task);
    }
    //Nothing to do before init
    return null;
  }
  /**
   * Write to the task item, if initialization has been called
   * @return toast text
   */
  protected abstract String writeDataAfterInit(TaskItem task);

  /**
   * Called when the inflation done
   */
  protected abstract void afterInflate();
}
