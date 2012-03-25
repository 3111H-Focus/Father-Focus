/**
 * Fragment for the timer fragment, redundent currently
 */

package hkust.comp3111h.focus.ui;
import hkust.comp3111h.focus.Adapter.TaskDnDAdapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ListAdapter;
import android.widget.LinearLayout;

import java.util.ArrayList;

import hkust.comp3111h.focus.R;


public class TaskManageFragment extends Fragment {
  private TaskDnDAdapter mAdapter;
  private DnDListView mListView;
  public View onCreateView(
      LayoutInflater inflater, 
      ViewGroup container,
      Bundle savedInstanceState) {
    if(container == null) {
      return null;
    }

    ArrayList<String> content = new ArrayList<String>(testContent.length);
    for(int i=0; i < testContent.length; i++) {
      content.add(testContent[i]);
    }

    LinearLayout layout = (LinearLayout)inflater.inflate(
        R.layout.taskmanagefrag,
        container,
        false);
    mAdapter = new TaskDnDAdapter(
        getActivity(), 
        new int[]{R.layout.task_item},
        new int[]{R.id.task_name},
        content);
        
    mListView =(DnDListView) layout.findViewById(R.id.dndlist);
    mListView.setAdapter(mAdapter); 
    mListView.setDropListener(mDropListener);
    mListView.setRemoveListener(mRemoveListener);
    return layout;
  }
  private DropListener mDropListener = new DropListener() {
    @Override
    public void onDrop(int from, int to) {
      mAdapter.onDrop(from,to);
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
      //Do nothing
    }
    public void onStartDrag(View itemView) {
      itemView.setVisibility(View.INVISIBLE);
      defaultBackgroundColor = itemView.getDrawingCacheBackgroundColor();
      itemView.setBackgroundColor(backgroundColor);
    }
    public void onStopDrag(View itemView) {
      itemView.setVisibility(View.VISIBLE);
      itemView.setBackgroundColor(defaultBackgroundColor);
    }
  };
  private static String[] testContent = {"Task 1", "Task 2", "Task 3"};
}



