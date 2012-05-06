/**
 * Fragment for the timer fragment, redundent currently
 */

package hkust.comp3111h.focus.ui;
import hkust.comp3111h.focus.R;
import hkust.comp3111h.focus.Activity.MainActivity;
import hkust.comp3111h.focus.Adapter.ArrayWheelAdapter;
import hkust.comp3111h.focus.Adapter.TaskListWheelAdapter;
import hkust.comp3111h.focus.Adapter.TaskWheelViewAdapter;
import hkust.comp3111h.focus.database.TaskDbAdapter;
import hkust.comp3111h.focus.database.TaskItem;
import hkust.comp3111h.focus.database.TaskListItem;
import hkust.comp3111h.focus.database.TimeItem;
import hkust.comp3111h.focus.Activity.FocusBaseActivity;
import hkust.comp3111h.focus.Adapter.TaskWheelViewAdapter;
import hkust.comp3111h.focus.Adapter.TaskListWheelAdapter;
import hkust.comp3111h.focus.locker.ScreenLocker;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import org.joda.time.DateTime;
import org.joda.time.Duration;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


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
  
  ScreenLocker screen_locker;

/*=====================================================
 * Initializations
 *======================================================*/

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    scrolling = true;
    mDbAdapter = ((FocusBaseActivity)getActivity()).getDbAdapter();
    String[] mTestArray = getActivity().getResources().getStringArray(R.array.procrastinator);    
    screen_locker = new ScreenLocker(getActivity(), mTestArray);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    Log.v("TimerFragment","Start CreateView");
    if (container == null) {
      return null;
    }
    timerView = (LinearLayout) inflater.inflate(R.layout.timerfrag, container,
        false);
    initWheels();
    initButton();
    Log.v("TimerFragment","End CreateView");
    return timerView;
  }
  
  //Methods for setting up the wheels
  private void initWheels() {
    TaskWheel = (WheelView) timerView.findViewById(R.id.wheel_two);
    TaskWheel.setCyclic(false);
    TaskWheel.setVisibleItems(5);
    HourWheel = (WheelView) timerView.findViewById(R.id.wheel_three);
    MinuteWheel = (WheelView) timerView.findViewById(R.id.wheel_four);
    SecondWheel = (WheelView) timerView.findViewById(R.id.wheel_five);
    HourWheel.setVisibleItems(1);
    MinuteWheel.setVisibleItems(1);
    SecondWheel.setVisibleItems(1);
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
    TaskListWheel.setCyclic(false);
    updateTaskListData();
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
  public void updateWheelData() {
    updateTaskListData();
    updateTaskWheel();
  }

  private void updateTaskListData() {
    ArrayList<TaskListItem> tlistItems = mDbAdapter.fetchAllTaskListsObjs(true);
    taskListWheelAdapter = new TaskListWheelAdapter( getActivity(),tlistItems);
    TaskListWheel.setViewAdapter(taskListWheelAdapter);
  }

  /**
   * Update the task wheel
   */
  private void updateTaskWheel() {
    if (!isTimerStart) {
      TaskListItem curTlist = taskListWheelAdapter.getItem(TaskListWheel.getCurrentItem());
      ArrayList<TaskItem> curTaskItems = mDbAdapter.fetchTasksObjInList(curTlist.taskListId(),true);
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
    hrWheelAdapter.setTextSize(30);
    minuteWheelAdapter.setTextSize(30);
    secondWheelAdapter.setTextSize(30);
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
          SharedPreferences userPref = PreferenceManager.getDefaultSharedPreferences(getActivity().getBaseContext());
          boolean use_lock=userPref.getBoolean("lockScreen", false);
          if (!use_lock) {
            isTimerStart = true;
            startTimer();
            setUI4Timer(true);  
          } else if (screen_locker.checkPolicy()) {
            isTimerStart = true;
            startTimer();
            setUI4Timer(true); 
            screen_locker.lock();         
          } else {
            screen_locker.setPolicy();  
          }
        } else {
          isTimerStart = false;
          stopTimer();
          mDbAdapter.updateEndTime(runningItemId);
          setUI4TaskSelection(true);
        }
      }
    });
  }
  /**
   * Transforms the user interface to task selection
   */
  private void setUI4TaskSelection(boolean animation) {
    stopOrStartButton.setText("Start Timer", TextView.BufferType.NORMAL);
    TaskListWheel.setVisibility(View.VISIBLE);
    TaskWheel.setVisibility(View.VISIBLE);
    if(animation) {
        Animation task_anim = AnimationUtils.loadAnimation(getActivity(), R.anim.wheel_task_in);
      Animation timer_anim = AnimationUtils.loadAnimation(getActivity(), R.anim.timer_out);
      TaskListWheel.startAnimation(task_anim);
      TaskWheel.startAnimation(task_anim);
      HourWheel.startAnimation(timer_anim);
      MinuteWheel.startAnimation(timer_anim);
      SecondWheel.startAnimation(timer_anim);
    }
    HourWheel.setVisibility(View.GONE);
    MinuteWheel.setVisibility(View.GONE);
    SecondWheel.setVisibility(View.GONE);
  }
  /* Invoked when the wheels turn into the timer */
  private void setUI4Timer(boolean animation) {
    stopOrStartButton.setText("Stop", TextView.BufferType.NORMAL);
    HourWheel.setVisibility(View.VISIBLE);
    MinuteWheel.setVisibility(View.VISIBLE);
    SecondWheel.setVisibility(View.VISIBLE);
    if(animation){
      Animation task_anim = AnimationUtils.loadAnimation(getActivity(), R.anim.wheel_task_out);
      Animation timer_anim = AnimationUtils.loadAnimation(getActivity(), R.anim.timer_in);
      TaskListWheel.startAnimation(task_anim);
      TaskWheel.startAnimation(task_anim);
      HourWheel.startAnimation(timer_anim);
      MinuteWheel.startAnimation(timer_anim);
      SecondWheel.startAnimation(timer_anim);
    }
    TaskListWheel.setVisibility(View.GONE);
    TaskWheel.setVisibility(View.GONE);
  }
  /*==========================================================
   * =========================Timer controls=================
   *==========================================================*/


  //Start the timer
  private synchronized void startTimer() {
    int curIndex = TaskWheel.getCurrentItem();
    if(curIndex < 0|| curIndex >= taskAdapter.getItemsCount()) {
      return;
    }
    selectedTask = taskAdapter.getItem(curIndex);
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
      startTime = runningItem.startTime();
      Log.d("TimerFragment","Running:"+runningItem.taskId());
      isTimerStart = true;
      setUI4Timer(false);
      mTimer = new Timer();
      mTimer.schedule(new TimerTask() {
        @Override
        public void run() {
          Log.d("TimerFragment","Timer running");
          updateTimerValues();
        }
      }, 1000, 1000);
    }
  }

  /**
   * Called when the fragment is visible to the user and actively 
   * running
   */
  @Override
  public void onResume() {
    super.onResume();
    updateTaskWheel();
    resumeTimer();
  }
  @Override 
  public  void onStart() {
    super.onStart();
    Log.v("TimerFragment", "LifeCycle: onStart");
  }


  @Override
  public String toString() {
    return "TimerFragment";
  }
  
}