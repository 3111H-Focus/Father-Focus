/**
 * Fragment for the timer fragment, redundent currently
 */

package hkust.comp3111h.focus.ui;

import hkust.comp3111h.focus.R;
import hkust.comp3111h.focus.Adapter.ArrayWheelAdapter;
import hkust.comp3111h.focus.database.TaskDbAdapter;
import hkust.comp3111h.focus.database.TaskItem;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.database.Cursor;
import android.os.Bundle;
import android.os.SystemClock;
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
  long baseTime;
  View timerView;
  WheelView WheelOne;
  WheelView WheelTwo;
  WheelView WheelThree;
  WheelView WheelFour;
  WheelView WheelFive;

  ArrayWheelAdapter<String> hrWheelAdapter;
  ArrayWheelAdapter<String> minuteWheelAdapter;
  ArrayWheelAdapter<String> secondWheelAdapter;
  ArrayWheelAdapter<String> taskListWheelAdapter;
  Button stopOrStartButton;
  boolean isTimerStart = false;
  //Dummy data for testing propose
  final String TaskLists[] = { "List 1", "List 2", "List 3" };
  final String Tasks[][] = new String[][] {
      new String[] { "Task 1", "Task 2", "Task 3", "Task 4", "Task 5" },
      new String[] { "Task 6", "Task 7", "Task 8", "Task 9", "Task 10" },
      new String[] { "Task 11", "Task 12", "Task 13", "Task 14", "Task 15" } };

  
  //Start the timer
  private void startTimer() {
    Log.d("Timer", "starting");
    baseTime = SystemClock.elapsedRealtime();
    isTimerStart = true;
    mTimer = new Timer();
    WheelThree.setCurrentItem(0, false);
    mTimer.schedule(new TimerTask() {
      @Override
      public void run() {
        long sec = (SystemClock.elapsedRealtime() - baseTime)/1000;
        Log.d("Timer", "Sec"+sec);
        WheelThree.setCurrentItem((int)sec / 3600, true);
        WheelFour.setCurrentItem((int)(sec % 3600) / 60, true);
        WheelFive.setCurrentItem((int)sec % 60, true);
      }
    }, 0, 1000);
  }

  //Stop the timer
  private void stopTimer() {
    mTimer.cancel();
    isTimerStart = false;
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

  private void setWheelForTimer() {
    WheelThree.setViewAdapter(hrWheelAdapter);
    WheelFour.setViewAdapter(minuteWheelAdapter);
    WheelFive.setViewAdapter(secondWheelAdapter);
  }

  private void setWheelForTask() {
    WheelOne.setViewAdapter(taskListWheelAdapter);
    int l_index = WheelOne.getCurrentItem();
    TaskDbAdapter db = new TaskDbAdapter(getActivity());
    db.open();
    Cursor c = db.fetchAllTaskLists();
    int c_index = c.getColumnIndex(TaskDbAdapter.KEY_TASK_TLID);
    for (c.moveToFirst(); l_index > 0; c.moveToNext()) {
      l_index--;
    }
    c = db.fetchAllTasksInList(c.getLong(c_index));
    ArrayList<String> tasks_al = new ArrayList<String>();
    int c_index_for_task = c.getColumnIndex(TaskDbAdapter.KEY_TASK_NAME);
    for (c.moveToFirst(); c.isAfterLast(); c.moveToNext()) {
        tasks_al.add(c.getString(c_index_for_task));
    }
    String[] tasks = new String[0];
    tasks = tasks_al.toArray(tasks);
    taskListWheelAdapter = new ArrayWheelAdapter<String>(getActivity(),
        tasks);
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    scrolling = true;
  }

  @Override
  public void onResume() {
    super.onResume();
  }

  /**
   * Update the task wheel
   */
  //not used
  private void updateTasks(WheelView tWheel, String Tasks[][], int index) {
    if (!isTimerStart) {
      ArrayWheelAdapter<String> adapter = new ArrayWheelAdapter<String>(
          getActivity(), Tasks[index]);
      adapter.setTextSize(18);
      tWheel.setViewAdapter(adapter);
      tWheel.setCurrentItem(Tasks[index].length / 2);
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    if (container == null) {
      return null;
    }
    timerView = (LinearLayout) inflater.inflate(R.layout.timerfrag, container,
        false);
    // For test purpose
    initWheelTwo(timerView);
    initWheelOne(timerView);
    initWheelThree(timerView);
    initWheelFour(timerView);
    initWheelFive(timerView);
    initButton(timerView);
    initializeTimeAdapters();
    return timerView;
  }

  private void initWheelOne(View timberView) {
    WheelOne = (WheelView) timerView.findViewById(R.id.wheel_one);
    WheelOne.setVisibleItems(3);
    TaskDbAdapter db = new TaskDbAdapter(getActivity());
    db.open();
    Cursor c = db.fetchAllTaskLists();
    ArrayList<String> task_lists_al = new ArrayList<String>();
    int index = c.getColumnIndex(TaskDbAdapter.KEY_TASKLIST_TLNAME);
    for (c.moveToFirst(); c.isAfterLast(); c.moveToNext()) {
      task_lists_al.add(c.getString(index));
    }
    String[] task_lists = new String[0];
    task_lists = task_lists_al.toArray(task_lists);
    taskListWheelAdapter = new ArrayWheelAdapter<String>(getActivity(),
        task_lists);
    WheelOne.setViewAdapter(taskListWheelAdapter);
    WheelOne.addChangingListener(new OnWheelChangedListener() {
      public void onChanged(WheelView wheel, int oldValue, int newValue) {
        if (!scrolling) {
          updateTasks(WheelTwo, Tasks, newValue);
        }
      }
    });
    WheelOne.addScrollingListener(new OnWheelScrollListener() {
      public void onScrollingStarted(WheelView wheel) {
        scrolling = true;
      }

      public void onScrollingFinished(WheelView wheel) {
        scrolling = false;
        updateTasks(WheelTwo, Tasks, WheelOne.getCurrentItem());
      }
    });
    WheelOne.setCurrentItem(1);
  }

  private void initWheelTwo(View timerView) {
    WheelTwo = (WheelView) timerView.findViewById(R.id.wheel_two);
    WheelTwo.setVisibleItems(5);
  }

  private void initWheelThree(View timerView) {
    WheelThree = (WheelView) timerView.findViewById(R.id.wheel_three);
  }
  
  private void initWheelFour(View timerView) {
	WheelFour = (WheelView) timerView.findViewById(R.id.wheel_four);
  }
  
  private void initWheelFive(View timerView) {
	WheelFive = (WheelView) timerView.findViewById(R.id.wheel_five);
  }

  /**
   * Set up the button
   */
  private void initButton(View timerView) {
    stopOrStartButton = (Button) timerView
        .findViewById(R.id.start_or_stop_button);
    stopOrStartButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        if (!isTimerStart) {
          isTimerStart = true;
          stopOrStartButton.setText("Stop", TextView.BufferType.NORMAL);
          setWheelForTimer();
          startTimer();
          onClickTransformation();
        } else {
          isTimerStart = false;
          stopTimer();
          stopOrStartButton.setText("Start Timer", TextView.BufferType.NORMAL);
          setWheelForTask();
          onClickReverseTransformation();
        }
      }
      
      /* Invoked when the wheels turn into the timer */
      private void onClickTransformation() {
    	WheelThree.setVisibility(View.VISIBLE);
    	WheelFour.setVisibility(View.VISIBLE);
    	WheelFive.setVisibility(View.VISIBLE);
    	Animation task_anim = AnimationUtils.loadAnimation(getActivity(), R.anim.wheel_task_out);
    	Animation timer_anim = AnimationUtils.loadAnimation(getActivity(), R.anim.timer_in);
    	WheelOne.startAnimation(task_anim);
    	WheelTwo.startAnimation(task_anim);
    	WheelThree.startAnimation(timer_anim);
    	WheelFour.startAnimation(timer_anim);
    	WheelFive.startAnimation(timer_anim);
    	WheelOne.setVisibility(View.GONE);
    	WheelTwo.setVisibility(View.GONE);
      }
      
      private void onClickReverseTransformation() {
    	WheelOne.setVisibility(View.VISIBLE);
      	WheelTwo.setVisibility(View.VISIBLE);
      	Animation task_anim = AnimationUtils.loadAnimation(getActivity(), R.anim.wheel_task_in);
    	Animation timer_anim = AnimationUtils.loadAnimation(getActivity(), R.anim.timer_out);
    	WheelOne.startAnimation(task_anim);
    	WheelTwo.startAnimation(task_anim);
    	WheelThree.startAnimation(timer_anim);
    	WheelFour.startAnimation(timer_anim);
    	WheelFive.startAnimation(timer_anim);
    	WheelThree.setVisibility(View.GONE);
    	WheelFour.setVisibility(View.GONE);
    	WheelFive.setVisibility(View.GONE);
      }
    });
  }

  @Override
  public String toString() {
    return "TimerFragment";
  }
  
  /*private class TimerService extends Service {

	public class LocalBinder extends Binder {
    
  	  public TimerService getService() {
        return TimerService.this;
  	  }
	}
	
	@Override
	public void onCreate() {
	  Log.i("created?", "yes");
	  sec = 0;
	  binder = new LocalBinder();
	  timer.schedule(new TimerTask() {

		@Override
		public void run() {
		  sec++;
		}
	  }, 0, 1000);
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
	  Log.i("started?", "yes");
	  return 1;
	}
	
	@Override
	public void onDestroy() {
	  
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
	  return binder;
	}
	
	public int getSec() {
    	return sec;
      }
      	
	private Timer timer = new Timer();
	private Binder binder;
	private int sec;
  }*/
}
