package hkust.comp3111h.focus.ui;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

import hkust.comp3111h.focus.ui.*;
import hkust.comp3111h.focus.Adapter.*;

public class MainViewPager extends ViewPager {
  public MainViewPager(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  @Override
  public void setAdapter(PagerAdapter adapter) {
    super.setAdapter(adapter);
  }
}
