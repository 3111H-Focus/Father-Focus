/**
 * Fragment for the statics fragment, redundent currently
 */

package hkust.comp3111h.focus.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;

import hkust.comp3111h.focus.R;
import hkust.comp3111h.focus.Activity.FocusBaseActivity;
import hkust.comp3111h.focus.database.TaskDbAdapter;
import hkust.comp3111h.focus.database.TimeItem;

public class StatisticsFragment extends Fragment {
  TaskDbAdapter mDbAdapter;
  ArrayList<TimeItem> timeItems;

  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mDbAdapter = ((FocusBaseActivity)getActivity()).getDbAdapter();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    if (container == null) {
      return null;
    }
    LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.statisfrag, container,
        false);
    return layout;
  }
  
}
