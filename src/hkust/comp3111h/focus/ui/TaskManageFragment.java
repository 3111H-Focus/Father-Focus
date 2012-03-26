/**
 * Fragment for the timer fragment, redundent currently
 */

package hkust.comp3111h.focus.ui;

import hkust.comp3111h.focus.R;
import hkust.comp3111h.focus.Adapter.TaskDnDAdapter;
import hkust.comp3111h.focus.database.TaskDbAdapter;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;
import android.graphics.Point;
import android.view.Display;
import android.util.DisplayMetrics;
import android.util.Log;
import hkust.comp3111h.focus.R;

public class TaskManageFragment extends Fragment {
  private TaskDnDAdapter mAdapter;
  private DnDListView mListView;

  public void updateList() {
    Log.v("Fragment", "updating");
    mAdapter.update();
    mAdapter.notifyDataSetChanged();
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    // TODO Auto-generated method stub
    super.onCreate(savedInstanceState);
    mAdapter = new TaskDnDAdapter(getActivity(),
        new int[] { R.layout.task_item },
        new int[] { R.id.task_name });
    mAdapter.update();
  }

  @Override
  public void onResume() {
    super.onResume();
    mAdapter.update();
  }

  public View onCreateView(LayoutInflater inflater,
      ViewGroup container, Bundle savedInstanceState) {
    if (container == null) {
      return null;
    }

    LinearLayout layout = (LinearLayout) inflater.inflate(
        R.layout.taskmanagefrag, container, false);

    mListView = (DnDListView) layout.findViewById(R.id.dndlist);
    mListView.setAdapter(mAdapter);
    mListView.setDropListener(mDropListener);
    mListView.setRemoveListener(mRemoveListener);

    return layout;
  }

  private DropListener mDropListener = new DropListener() {
    @Override
    public void onDrop(int from, int to) {
      mAdapter.onDrop(from, to);
      mListView.invalidateViews();
    }
  };

  private RemoveListener mRemoveListener = new RemoveListener() {
    @Override
    public void onRemove(int which) {
      mAdapter.onRemove(which);
      mListView.invalidateViews();
    }
  };
  private DragListener mDragListener = new DragListener() {
    int backgroundColor = 0xe0103010;
    int defaultBackgroundColor;

    @Override
    public void onDrag(int x, int y, ListView listView) {
      // Do nothing
    }

    public void onStartDrag(View itemView) {
      itemView.setVisibility(View.INVISIBLE);
      defaultBackgroundColor = itemView
          .getDrawingCacheBackgroundColor();
      itemView.setBackgroundColor(backgroundColor);
    }

    public void onStopDrag(View itemView) {
      itemView.setVisibility(View.VISIBLE);
      itemView.setBackgroundColor(defaultBackgroundColor);
    }
  };

}
