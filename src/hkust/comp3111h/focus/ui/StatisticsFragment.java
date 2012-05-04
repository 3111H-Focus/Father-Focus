/**
 * Fragment for the statics fragment, redundent currently
 */

package hkust.comp3111h.focus.ui;

import hkust.comp3111h.focus.R;
import hkust.comp3111h.focus.Activity.FocusBaseActivity;
import hkust.comp3111h.focus.database.TaskDbAdapter;
import hkust.comp3111h.focus.database.TimeItem;

import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

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
    Bitmap b = Bitmap.createBitmap(1000, 1000, Config.ARGB_8888);
    Canvas c = new Canvas(b);
    c.drawColor(Color.BLACK);
    return layout;
  }
  
}
