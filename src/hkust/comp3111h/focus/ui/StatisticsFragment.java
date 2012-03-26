/**
 * Fragment for the statics fragment, redundent currently
 */

package hkust.comp3111h.focus.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;
import android.widget.LinearLayout;

import hkust.comp3111h.focus.R;

public class StatisticsFragment extends Fragment {
  public View onCreateView(LayoutInflater inflater,
      ViewGroup container, Bundle savedInstanceState) {
    if (container == null) {
      return null;
    }
    return (LinearLayout) inflater.inflate(R.layout.statisfrag,
        container, false);
  }
}
