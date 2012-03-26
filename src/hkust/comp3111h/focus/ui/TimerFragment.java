/**
 * Fragment for the timer fragment, redundent currently
 */

package hkust.comp3111h.focus.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ImageView;
import android.widget.Button;
import android.widget.TextView;


import hkust.comp3111h.focus.R;
import hkust.comp3111h.focus.Adapter.*;

public class TimerFragment extends Fragment {
  // scrolling flag
  private boolean scrolling = false;
  View timerView;
  WheelView WheelOne;
  WheelView WheelTwo;
  WheelView WheelThree;

  Button stopOrStartButton;
  boolean isTimerStart = false;
  final String TaskLists[] = {"List 1", "List 2", "List 3"};
  final String Tasks[][] = new String[][] {
    new String[] {"Task 1", "Task 2", "Task 3", "Task 4", "Task 5"},
    new String[] {"Task 6", "Task 7", "Task 8", "Task 9", "Task 10"},
    new String[] {"Task 11", "Task 12", "Task 13", "Task 14", "Task 15"}
  };

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    boolean scrolling = true;
  }
  @Override
  public void onResume() {
    super.onResume();
  }

  /**
   * Update the task wheel
   */
  private void updateTasks(WheelView tWheel, String Tasks[][],
      int index) {
    ArrayWheelAdapter<String> adapter = new ArrayWheelAdapter<String>(
        getActivity(), Tasks[index]);
    adapter.setTextSize(18);
    tWheel.setViewAdapter(adapter);
    tWheel.setCurrentItem(Tasks[index].length / 2);
  }

  @Override
  public View onCreateView(LayoutInflater inflater,
      ViewGroup container, Bundle savedInstanceState) {
    if (container == null) {
      return null;
    }
<<<<<<< HEAD
    timerView = (LinearLayout) inflater.inflate(R.layout.timerfrag,
        container, false);
    timerView = (LinearLayout) inflater.inflate(R.layout.timerfrag,
        container, false);
    final WheelView tListWheel = (WheelView) timerView
        .findViewById(R.id.TaskLists);
    final String TaskLists[] = { "List 1", "List 2", "List 3" };
    tListWheel.setVisibleItems(3);
    tListWheel.setViewAdapter(new ArrayWheelAdapter<String>(
        getActivity(), TaskLists));

    // For test purpose
    final String Tasks[][] = new String[][] {
        new String[] { "Task 1", "Task 2", "Task 3", "Task 4",
            "Task 5" },
        new String[] { "Task 6", "Task 7", "Task 8", "Task 9",
            "Task 10" },
        new String[] { "Task 11", "Task 12", "Task 13", "Task 14",
            "Task 15" } };

    final WheelView taskWheel = (WheelView) timerView
        .findViewById(R.id.Tasks);
    taskWheel.setVisibleItems(5);

    tListWheel.addChangingListener(new OnWheelChangedListener() {
      public void onChanged(WheelView wheel, int oldValue,
          int newValue) {
        if (!scrolling) {
          updateTasks(taskWheel, Tasks, newValue);
=======
    timerView =  (LinearLayout)inflater.inflate(
        R.layout.timerfrag,
        container,
        false);
    //For test purpose
    initWheelTwo(timerView);
    initWheelOne(timerView);
    initWheelThree(timerView);
    initButton(timerView);
    return timerView;
  }

  private void initWheelOne(View timberView){
    WheelOne = (WheelView) timerView.findViewById(R.id.wheel_one);
    WheelOne.setVisibleItems(3);
    WheelOne.setViewAdapter(new ArrayWheelAdapter<String>(getActivity(),TaskLists));
    WheelOne.addChangingListener(new OnWheelChangedListener() {
      public void onChanged(WheelView wheel, int oldValue, int newValue) {
        if(!scrolling) {
          updateTasks(WheelTwo, Tasks, newValue);
>>>>>>> 0288dd96ac6e3abd5c8677d09d63cebcb810074f
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
    WheelThree.setViewAdapter(new ArrayWheelAdapter<String>(getActivity(),new String[]{"haha","hehe"}));
  }

  private void initButton(View timerView) {
    stopOrStartButton = (Button) timerView.findViewById(R.id.start_or_stop_button);
    stopOrStartButton.setOnClickListener(new OnClickListener(){
      @Override
      public void onClick(View v) {
        if(!isTimerStart){
          WheelThree.setVisibility(View.VISIBLE);
          isTimerStart = true;
          stopOrStartButton.setText("Stop",TextView.BufferType.NORMAL);
        }else{
          WheelThree.setVisibility(View.GONE);
          isTimerStart = false;
          stopOrStartButton.setText("Start Timer", TextView.BufferType.NORMAL);
        }
        
      }
    });
  }
}
