/**
 * Fragment for the timer fragment, redundent currently
 */

package hkust.comp3111h.focus.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ImageView;

import hkust.comp3111h.focus.R;
import hkust.comp3111h.focus.Adapter.*;

public class TimerFragment extends Fragment {
  // scrolling flag
  private boolean scrolling = false;
  View timerView;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
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
        }
      }
    });
    tListWheel.addScrollingListener(new OnWheelScrollListener() {
      public void onScrollingStarted(WheelView wheel) {
        scrolling = true;
      }

      public void onScrollingFinished(WheelView wheel) {
        scrolling = false;
        updateTasks(taskWheel, Tasks, tListWheel.getCurrentItem());
      }
    });
    tListWheel.setCurrentItem(1);
    return timerView;
  }
}
