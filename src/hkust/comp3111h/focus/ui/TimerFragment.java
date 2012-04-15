/**
 * Fragment for the timer fragment, redundent currently
 */

package hkust.comp3111h.focus.ui;
import org.joda.time.DateTime;
import org.joda.time.Duration;

import hkust.comp3111h.focus.R;
import hkust.comp3111h.focus.Adapter.ArrayWheelAdapter;
import hkust.comp3111h.focus.database.TaskDbAdapter;
import hkust.comp3111h.focus.database.TaskItem;
import hkust.comp3111h.focus.database.TaskListItem;
import hkust.comp3111h.focus.database.TimeItem;
import hkust.comp3111h.focus.Activity.MainActivity;
import hkust.comp3111h.focus.Adapter.TaskWheelViewAdapter;
import hkust.comp3111h.focus.Adapter.TaskListWheelAdapter;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.database.Cursor;
import android.os.Bundle;
import android.os.SystemClock;
import android.os.PowerManager;
import android.os.Handler;
import android.os.Message;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.util.Log;

public class TimerFragment extends Fragment {
  // scrolling flag
  private boolean scrolling = false;
  private Timer mTimer = new Timer();
  private long runningItemId;
  DateTime startTime;
  View timerView;
  WheelView TaskListWheel;
  WheelView TaskWheel;
  WheelView HourWheel;
  WheelView MinuteWheel;
  WheelView SecondWheel;

  ArrayWheelAdapter<String> hrWheelAdapter;
  ArrayWheelAdapter<String> minuteWheelAdapter;
  ArrayWheelAdapter<String> secondWheelAdapter;
  TaskListWheelAdapter taskListWheelAdapter;
  TaskWheelViewAdapter taskAdapter;
  Button stopOrStartButton;

  TaskDbAdapter mDbAdapter;
  TaskItem selectedTask;
  boolean isTimerStart = false;

/*=====================================================
 * Initializations
 *======================================================*/

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    scrolling = true;
    mDbAdapter = ((MainActivity)getActivity()).getDbAdapter();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    if (container == null) {
      return null;
    }
    timerView = (LinearLayout) inflater.inflate(R.layout.timerfrag, container,
        false);
    initWheels();
    initButton();
    return timerView;
  }
  
  //Methods for setting up the wheels
  private void initWheels() {
    TaskWheel = (WheelView) timerView.findViewById(R.id.wheel_two);
    TaskWheel.setVisibleItems(5);
    HourWheel = (WheelView) timerView.findViewById(R.id.wheel_three);
    MinuteWheel = (WheelView) timerView.findViewById(R.id.wheel_four);
    SecondWheel = (WheelView) timerView.findViewById(R.id.wheel_five);
    initTaskListWheel();
    updateTaskWheel();
    initializeTimeAdapters();
    HourWheel.setViewAdapter(hrWheelAdapter);
    MinuteWheel.setViewAdapter(minuteWheelAdapter);
    SecondWheel.setViewAdapter(secondWheelAdapter);
  }
  
  private void initTaskListWheel() {
    TaskListWheel = (WheelView) timerView.findViewById(R.id.wheel_one);
    TaskListWheel.setVisibleItems(3);
    ArrayList<TaskListItem> tlistItems = mDbAdapter.fetchAllTaskListsObjs(true);
    taskListWheelAdapter = new TaskListWheelAdapter( getActivity(),tlistItems);
    TaskListWheel.setViewAdapter(taskListWheelAdapter);
    TaskListWheel.addChangingListener(new OnWheelChangedListener() {
      public void onChanged(WheelView wheel, int oldValue, int newValue) {
        if (!scrolling) {
          updateTaskWheel();
        }
      }
    });
    TaskListWheel.addScrollingListener(new OnWheelScrollListener() {
      public void onScrollingStarted(WheelView wheel) {
        scrolling = true;
      }
      public void onScrollingFinished(WheelView wheel) {
        scrolling = false;
        updateTaskWheel();
      }
    });
    TaskListWheel.setCurrentItem(1);
  }

  /**
   * Update the task wheel
   */
  private void updateTaskWheel() {
    if (!isTimerStart) {
      TaskListItem curTlist = taskListWheelAdapter.getItem(TaskListWheel.getCurrentItem());
      ArrayList<TaskItem> curTaskItems = mDbAdapter.fetchTasksObjInList(curTlist.taskListId());
      taskAdapter = new TaskWheelViewAdapter(getActivity(), curTaskItems);
      taskAdapter.setTextSize(18);
      TaskWheel.setViewAdapter(taskAdapter);
      TaskWheel.setCurrentItem(taskAdapter.getItemsCount() / 2);
    }
  }

  //Initialize the adapter for the timer
  private void initializeTimeAdapters() {
    String[] seconds = new String[60];
    String[] minutes = new String[60];
    for (int i = 0; i < 60; i++) {
      seconds[i] = "" + i;
      minutes[i] = "" + i;
    }
    secondWheelAdapter = new ArrayWheelAdapter<String>(getActivity(), seconds);
    minuteWheelAdapter = new ArrayWheelAdapter<String>(getActivity(), minutes);
    String[] hrs = new String[100];
    for (int i = 0; i < 100; i++) {
      hrs[i] = "" + i;
    }
    hrWheelAdapter = new ArrayWheelAdapter<String>(getActivity(), hrs);
  }


  /**
   * Set up the button
   */
  private void initButton() {
    stopOrStartButton = (Button) timerView
        .findViewById(R.id.start_or_stop_button);
    stopOrStartButton.setOnClickListener(new OnClickListener() {
      @Override
      public synchronized void onClick(View v) {
        if (!isTimerStart) {
          isTimerStart = true;
          stopOrStartButton.setText("Stop", TextView.BufferType.NORMAL);
          startTimer();
          transform2Timer();
        } else {
          isTimerStart = false;
          stopTimer();
          stopOrStartButton.setText("Start Timer", TextView.BufferType.NORMAL);
          mDbAdapter.updateEndTime(runningItemId);
          transform2TaskSelection();
        }
      }
    });
  }
  /**
   * Transforms the user interface to task selection
   */
  private void transform2TaskSelection() {
    TaskListWheel.setVisibility(View.VISIBLE);
      TaskWheel.setVisibility(View.VISIBLE);
      Animation task_anim = AnimationUtils.loadAnimation(getActivity(), R.anim.wheel_task_in);
    Animation timer_anim = AnimationUtils.loadAnimation(getActivity(), R.anim.timer_out);
    TaskListWheel.startAnimation(task_anim);
    TaskWheel.startAnimation(task_anim);
    HourWheel.startAnimation(timer_anim);
    MinuteWheel.startAnimation(timer_anim);
    SecondWheel.startAnimation(timer_anim);
    HourWheel.setVisibility(View.GONE);
    MinuteWheel.setVisibility(View.GONE);
    SecondWheel.setVisibility(View.GONE);
  }
  /* Invoked when the wheels turn into the timer */
  private void transform2Timer() {
    HourWheel.setVisibility(View.VISIBLE);
    MinuteWheel.setVisibility(View.VISIBLE);
    SecondWheel.setVisibility(View.VISIBLE);
    Animation task_anim = AnimationUtils.loadAnimation(getActivity(), R.anim.wheel_task_out);
    Animation timer_anim = AnimationUtils.loadAnimation(getActivity(), R.anim.timer_in);
    TaskListWheel.startAnimation(task_anim);
    TaskWheel.startAnimation(task_anim);
    HourWheel.startAnimation(timer_anim);
    MinuteWheel.startAnimation(timer_anim);
    SecondWheel.startAnimation(timer_anim);
    TaskListWheel.setVisibility(View.GONE);
    TaskWheel.setVisibility(View.GONE);
  }
  /*==========================================================
   * =========================Timer controls=================
   *==========================================================*/


  //Start the timer
  private synchronized void startTimer() {
    selectedTask = taskAdapter.getItem(TaskWheel.getCurrentItem());
    if(selectedTask!=null) {
      Log.d("Timer", "starting");
      startTime = new DateTime();
      runningItemId = mDbAdapter.createTime(selectedTask.taskId());
      isTimerStart = true;
      mTimer = new Timer();
      mTimer.schedule(new TimerTask() {
        @Override
        public void run() {
          updateTimerValues();
        }
      }, 1000, 1000);
    } else {
      //TODO: toast to notify user
    }
  }

  protected synchronized void updateTimerValues() {
    if(startTime!=null) {
      Duration duration = new Duration(startTime, new DateTime());
      Log.v("TimerFragment","Duration is "+duration.toString());
      HourWheel.setCurrentItem((int)duration.getStandardHours(),true);
      MinuteWheel.setCurrentItem((int)duration.getStandardMinutes(),true);
      SecondWheel.setCurrentItem((int)duration.getStandardSeconds(),true);
    }
  }

  /**
   * When user click the stop button. This means that this time 
   * interval is done. Then, the program should upate the corresponding 
   * time entry in the database.
   */
  private synchronized void stopTimer() {
    mTimer.cancel();
    isTimerStart = false;
  }
  /*=================================================
   * Life cycle control
   *=================================================*/
  /**
   * Physically stop the timer when it is paused
   * But logically, it is running
   */
   @Override
   public void onPause() {
     super.onPause();
     synchronized(this) {
       pauseTimer();
     }
   }

  /**
   * Pause timer means, the user leave the view, so that the 
   * physical timer should be stoped, but logically, the task
   * is still in progress. 
   * This funciton here is just to stop the timer when the user
   * is not looking at the fragment, for efficiency purpose
   */
  private synchronized void pauseTimer() {
    if(mTimer!=null) {
      mTimer.cancel();
      mTimer=null;
    }
  }

  /**
   * This function should be called when the user returns from the 
   * pauseTimer(). That is when the user return to this fragment when 
   * this timer is logically running, get the data from the database 
   * and make sure that it is the status at the time of quit
   */
  private synchronized void resumeTimer() {
    TimeItem runningItem = mDbAdapter.getRunningTimeItem();
    if(runningItem!=null) {
      stopOrStartButton.setText("Stop", TextView.BufferType.NORMAL);
      runningItemId = runningItem.timeId();
      Log.d("TimerFragment","Running:"+runningItem.taskId());
      Log.d("TimerFragment","timeId:"+runningItem.taskId());
      selectedTask = mDbAdapter.fetchTaskObj(runningItem.taskId());
      isTimerStart = true;
      transform2Timer();
      mTimer = new Timer();
      mTimer.schedule(new TimerTask() {
        @Override
        public void run() {
          updateTimerValues();
        }
      }, 1000, 1000);
    }
  }

  @Override
  public void onResume() {
    super.onResume();
    resumeTimer();
  }


  @Override
  public String toString() {
    return "TimerFragment";
  }
  
}
