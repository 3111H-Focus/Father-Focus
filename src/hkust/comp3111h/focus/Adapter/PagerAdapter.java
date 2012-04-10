package hkust.comp3111h.focus.Adapter;

import java.util.List;
import android.util.Log;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import hkust.comp3111h.focus.R;

/**
 * The PagerAdapter serves the fragment when paging
 */
public class PagerAdapter extends FragmentPagerAdapter implements TitleProvider {
  private List<Fragment> mFragments;

  /**
   * constructor
   * 
   * @param fm
   * @param fragments
   */
  public PagerAdapter(FragmentManager fm, List<Fragment> fragments) {
    super(fm);
    mFragments = fragments;
  }

  @Override
  public Fragment getItem(int position) {
    return this.mFragments.get(position);
  }

  @Override
  public int getCount() {
    return this.mFragments.size();
  }
  @Override
  public String getTitle(int position) {
    Log.d("PagerAdapter", "tabposition is "+position);
    switch(position) {
      case 0:
        return "Task Manager";
      case 1:
        return "Timer";
      case 2:
        return "Statistics";
      default:
        return "heihei";
    }
  }
}
