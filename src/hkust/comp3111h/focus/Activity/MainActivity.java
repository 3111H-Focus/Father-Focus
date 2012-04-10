/**
 * @file MainActivity.java
 * The main activity that will be shown when user start up
 * Three basic framents navigated by tabs
 * Using the library of ActionBarSherlock
 */
package hkust.comp3111h.focus.Activity;
import hkust.comp3111h.focus.ui.QuickAddDialog;


import hkust.comp3111h.focus.R;
import hkust.comp3111h.focus.Adapter.PagerAdapter;
import hkust.comp3111h.focus.database.TaskDbAdapter;
import hkust.comp3111h.focus.Adapter.TaskDnDAdapter;
import hkust.comp3111h.focus.ui.MainMenuPopover;
import hkust.comp3111h.focus.ui.MainMenuPopover.MainMenuListener;
import hkust.comp3111h.focus.ui.StatisticsFragment;
import hkust.comp3111h.focus.ui.TaskManageFragment;
import hkust.comp3111h.focus.ui.TimerFragment;
import hkust.comp3111h.focus.ui.TitlePageIndicator;
import hkust.comp3111h.focus.ui.FragmentPopover;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import android.util.Log;
import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActionBar;
import android.support.v4.app.ActionBar.Tab;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;
import android.support.v4.view.MenuItem.OnMenuItemClickListener;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.PopupWindow.OnDismissListener;

public class MainActivity extends FragmentActivity implements
    ActionBar.TabListener, ViewPager.OnPageChangeListener, MainMenuListener {
  // determine honecome or not
  static final int DIALOG_QUICK_ADD = 0;


  private final Handler handler = new Handler();
  private ViewPager mViewPager;
  private PagerAdapter mPagerAdapter;
  private ImageView mainMenu;
  private TextView listTitle;
  private View listsNav;
  private ImageView listsNavDisclosure;

  private MenuItem addTaskMenuItem;

  private MainMenuPopover mainMenuPopover;
  private FragmentPopover listsPopover;

  // Actionbar set up
  private boolean useLogo = false;
  private boolean showHomeUp = false;
  private TaskDbAdapter mDbAdapter;
  private List<Fragment> fragments;

  Intent addTaskIntent;

  public TaskDbAdapter getDbAdapter() {
    return mDbAdapter;
  }
  /**
   * Called when the activity is first created Initialize Environment
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.pagerlayout); // Basically a easy linear layout

    //****************************************************
    //************Initialize the action bar here**********
    //****************************************************
    final ActionBar ab = getSupportActionBar();
    ab.setDisplayOptions(0, ActionBar.DISPLAY_SHOW_TITLE);
    ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
    ab.setCustomView(R.layout.header_actionbar_layout);

    //Main menu button setting up
    mainMenu = (ImageView) ab.getCustomView().findViewById(R.id.main_menu);
    mainMenu.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        mainMenu.setSelected(true);
        mainMenuPopover.show(v);
      }
    });

    //List title, showing the active list
    listsNavDisclosure = (ImageView) ab.getCustomView().findViewById(R.id.list_disclosure_arrow);
    listTitle = (TextView) ab.getCustomView().findViewById(R.id.list_title);
    listsNav = ab.getCustomView().findViewById(R.id.taskLists);
    listsNav.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        setListsDropdownSelected(true);
        listsPopover.show(v);
      }
    });

    //***************************************************************
    //******end of action bar
    //**************************************************************
    mDbAdapter = new TaskDbAdapter(this);
    mDbAdapter.open();
    // Initialise ViewPager
    this.initialiseViewPager();
    createMainMenuPopover();
  }
  private void setListsDropdownSelected(boolean selected) {
    int oldTextColor = listTitle.getTextColors().getDefaultColor();
    int textStyle = (selected ? R.style.TextAppearance_ActionBar_ListsHeader_Selected:R.style.TextAppearance_ActionBar_ListsHeader);
    listTitle.setTextAppearance(this, textStyle);
    listsNav.setBackgroundColor(selected ? oldTextColor: android.R.color.transparent);
    listsNavDisclosure.setSelected(selected);
  }
  public void initialiseListsPopover() {
    listsPopover = new FragmentPopover(this, R.layout.tlist_popover_layout);
    listsPopover.setOnDismissListener(new OnDismissListener() {
      @Override
      public void onDismiss() {
        setListsDropdownSelected(false);
      }
    });
  }

  /**
   * Initialize ViewPager
   */

  private void initialiseViewPager() {
    fragments = new Vector<Fragment>();
    fragments
        .add(Fragment.instantiate(this, TaskManageFragment.class.getName()));
    fragments.add(Fragment.instantiate(this, TimerFragment.class.getName()));
    fragments
        .add(Fragment.instantiate(this, StatisticsFragment.class.getName()));
    this.mPagerAdapter = new PagerAdapter(super.getSupportFragmentManager(),
        fragments);
    this.mViewPager = (ViewPager) super.findViewById(R.id.viewpager);
    this.mViewPager.setAdapter(this.mPagerAdapter);
    this.mViewPager.setOnPageChangeListener(this);
    //Bind the title indicator to the adapter
    TitlePageIndicator titleIndicator = (TitlePageIndicator)findViewById(R.id.titleIndicator);
    titleIndicator.setViewPager(this.mViewPager);
  }

  @Override
  protected Dialog onCreateDialog(int id) {
    Dialog dialog;
    switch(id) {
      case DIALOG_QUICK_ADD:
        dialog = new QuickAddDialog(this,(TaskManageFragment)fragments.get(0));
        break;
      default:
        dialog=null;
    }
    return dialog;
  }

  public void showQuickAddDialog() {
    showDialog(DIALOG_QUICK_ADD);
  }

  /**
   * Creating the action bar menu
   * 
   * @param menu
   */
  /*
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    //getMenuInflater().inflate(R.menu.action_bar_menu, menu);
    // find the buttons
    mainMenu = (ImageButton) menu.findItem(R.id.main_menu).getActionView();
    mainMenu.setImageResource(R.drawable.menu_button_icon);
    addTaskMenuItem = menu.findItem(R.id.add_task);
    mainMenu.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        mainMenuPopover.show(v);
      }
    });
    addTaskMenuItem.setOnMenuItemClickListener(new OnMenuItemClickListener() {
      // TODO: propriate listener
      @Override
      public boolean onMenuItemClick(MenuItem item) {
        addTaskIntent = new Intent(MainActivity.this, AddTaskActivity.class);
        startActivityForResult(addTaskIntent, 0); // 0 just a random
                                                  // requestCode.
        return false;
      }
    });
    return super.onCreateOptionsMenu(menu);
  }
  */

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    Log.v("Activity result", "requestCode is" + requestCode);
    if (resultCode == RESULT_CANCELED) {
      Long rowId = data.getLongExtra(TaskDbAdapter.KEY_TASK_TID, 0);
      if (!rowId.equals(new Long(0))) {
        mDbAdapter.deleteTask(rowId);
      }
    }
    if (requestCode == 0) {
      ((TaskManageFragment) mPagerAdapter.getItem(0)).updateList();
    }
  }

  private void createMainMenuPopover() {
    int layout = R.layout.main_menu_popover;
    mainMenuPopover = new MainMenuPopover(this, layout);
    mainMenuPopover.setMenuListener(this);
    mainMenuPopover.setOnDismissListener(new OnDismissListener() {
      @Override
      public void onDismiss() {
        mainMenu.setSelected(false);
      }
    });
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    return super.onOptionsItemSelected(item);
  }

  @Override
  protected void onPause() {
    super.onPause();
    if(mainMenuPopover!=null) {
      mainMenuPopover.dismiss();
    }
  }

  @Override
  public void mainMenuItemSelected(int item, Intent customIntent) {
    // TODO implmenent the main menu
  }

  public void onTabReselected(Tab tab, FragmentTransaction ft) {
    // TODO Auto-generated method stub
  }

  public void onTabSelected(Tab tab, FragmentTransaction ft) {
    // TODO Auto-generated method stub
    /*
    if (mViewPager == null) {
      this.initialiseViewPager();
    }
    mViewPager.setCurrentItem(tab.getPosition());
    */
  }

  public void onTabUnselected(Tab tab, FragmentTransaction ft) {
    // TODO Auto-generated method stub
  }

  public void onPageScrolled(int position, float positionOffset,
      int positionOffsetPixels) {
    // TODO Auto-generated method stub

  }

  public void onPageSelected(int position) {
    // TODO Auto-generated method stub
    /*
    final ActionBar ab = getSupportActionBar();
    ab.setSelectedNavigationItem(position);
    */

  }

  public void onPageScrollStateChanged(int state) {
    // TODO Auto-generated method stub

  }
}
