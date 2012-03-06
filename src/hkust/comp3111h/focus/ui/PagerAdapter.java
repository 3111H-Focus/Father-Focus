package hkust.comp3111h.focus.ui;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * The PagerAdapter serves the fragment when paging
 */
public class PagerAdapter extends FragmentPagerAdapter {
  private List<Fragment> mFragments;
  /**
   * constructor
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
}
