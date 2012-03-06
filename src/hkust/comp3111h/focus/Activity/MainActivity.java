package hkust.comp3111h.focus.activity;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import android.util.Log;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TabHost;
import android.widget.TabHost.TabContentFactory;

import hkust.comp3111h.focus.R;
import hkust.comp3111h.focus.ui.*;

public class MainActivity extends FragmentActivity implements 
TabHost.OnTabChangeListener, ViewPager.OnPageChangeListener {
  private TabHost mTabHost;
  private ViewPager mViewPager;
  private HashMap<String, TabInfo> mapTabInfo = 
      new HashMap<String, MainActivity.TabInfo>();
  private PagerAdapter mPagerAdapter;

  /**
   * Maintains extrinsic info of a tab's construct
   */
  private class TabInfo {
    private String tag;
    private Class<?> clss;
    private Bundle args;
    private Fragment fragment;
    TabInfo(String tag, Class<?> clazz, Bundle args) {
      this.tag = tag;
      this.clss = clazz;
      this.args = args;
    }
  }
  /**
   * A simple factory that currently returns dummy views to the Tabhost
   */
  class TabFactory implements TabContentFactory {
    private final Context mContext;

    public TabFactory(Context context) {
      mContext = context;
    }

    public View createTabContent(String tag) {
      View v = new View(mContext);
      v.setMinimumWidth(0);
      v.setMinimumHeight(0);
      return v;
    }
  }

  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    //Inflate the layout
    setContentView(R.layout.pagerlayout);
    //Initialise the TabHost
    this.initialiseTabHost(savedInstanceState);
    if(savedInstanceState != null) {
      //set the tab as the saved state
      mTabHost.setCurrentTabByTag(savedInstanceState.getString("tab"));
    }
    //Initialise ViewPager
    this.initialiseViewPager();
  }

  protected void onSaveInstanceState(Bundle outState) {
    //save the tab selected
    outState.putString("tab", mTabHost.getCurrentTabTag());
    super.onSaveInstanceState(outState);
  }

  /**
   * Initialise ViewPager
   */
  private void initialiseViewPager() {
    List<Fragment> fragments = new Vector<Fragment>();
    fragments.add(Fragment.instantiate(this, TaskManageFragment.class.getName()));
    fragments.add(Fragment.instantiate(this, TimerFragment.class.getName()));
    fragments.add(Fragment.instantiate(this, StatisticsFragment.class.getName()));
    this.mPagerAdapter = new PagerAdapter(super.getSupportFragmentManager(), fragments);
    this.mViewPager = (ViewPager)super.findViewById(R.id.viewpager);
    this.mViewPager.setAdapter(this.mPagerAdapter);
    this.mViewPager.setOnPageChangeListener(this);
  }

  /**
   * Initialise the Tab Host
   */
  private void initialiseTabHost(Bundle args) {
    mTabHost = (TabHost) findViewById(android.R.id.tabhost);
    mTabHost.setup();
    TabInfo tabInfo = null;
    MainActivity.AddTab(
        this, 
        this.mTabHost,
        this.mTabHost.newTabSpec("Tasks").setIndicator("Tasks"), 
        (tabInfo = new TabInfo("Tasks", TimerFragment.class, args)));
    this.mapTabInfo.put(tabInfo.tag, tabInfo);

    MainActivity.AddTab(
        this, 
        this.mTabHost,
        this.mTabHost.newTabSpec("Timer").setIndicator("Timer"), 
        (tabInfo = new TabInfo("Timer", TimerFragment.class, args)));
    this.mapTabInfo.put(tabInfo.tag, tabInfo);

    MainActivity.AddTab(
        this, 
        this.mTabHost,
        this.mTabHost.newTabSpec("Statistics").setIndicator("Statistics"), 
        (tabInfo = new TabInfo("Statistics", StatisticsFragment.class, args)));
    this.mapTabInfo.put(tabInfo.tag, tabInfo);

    //this.onTabChanged("Timer");
    mTabHost.setOnTabChangedListener(this);
  }

  /**
   * Add Tab content to the Tabhost
   * @param activity
   * @param tabHost
   * @param tabSpec
   * @Param clss
   * @Param args
   */
  private static void AddTab(
      MainActivity activity, 
      TabHost tabHost, 
      TabHost.TabSpec tabSpec,
      TabInfo tabInfo) {
    tabSpec.setContent(activity.new TabFactory(activity));
    tabHost.addTab(tabSpec);
  }

  public void onTabChanged(String tag) {
    //TabInfo newTab = this.mapTabInfo.get(tag);
    int pos = this.mTabHost.getCurrentTab();
    this.mViewPager.setCurrentItem(pos);
  }

  @Override
  public void onPageScrolled(
      int position, 
      float positionOffset,
      int positionOffsetPixels) {

  }

  @Override
  public void onPageSelected(int position) {
    this.mTabHost.setCurrentTab(position);
  }

  @Override
  public void onPageScrollStateChanged(int state) {

  }
}

