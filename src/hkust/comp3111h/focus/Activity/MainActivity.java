/**
 * @file MainActivity.java
 * The main activity that will be shown when user start up
 * Three basic framents navigated by tabs
 * Using the library of ActionBarSherlock
 */
package hkust.comp3111h.focus.Activity;

import hkust.comp3111h.focus.R;
import hkust.comp3111h.focus.Adapter.MainPagerAdapter;
import hkust.comp3111h.focus.Adapter.TaskListSidebarAdapter;
import hkust.comp3111h.focus.database.TaskListItem;
import hkust.comp3111h.focus.ui.AddTaskListDialog;
import hkust.comp3111h.focus.ui.MainMenuPopover;
import hkust.comp3111h.focus.ui.QuickAddDialog;
import hkust.comp3111h.focus.ui.StatisticsFragment;
import hkust.comp3111h.focus.ui.TaskManageFragment;
import hkust.comp3111h.focus.ui.TimerFragment;
import hkust.comp3111h.focus.ui.TitlePageIndicator;
import hkust.comp3111h.focus.ui.StatisticView;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;

public class MainActivity extends FocusBaseActivity {
  public static final String BROADCAST_REQUEST_EVENT_REFRESH = "hkust.comp3111h.focus.REQUEST_EVENT_REFRESH";
  // determine honecome or not
  static final int DIALOG_QUICK_ADD = 0;
  static final int DIALOG_ADD_TLIST = 1;

  private final Handler handler = new Handler();
  private ViewPager mViewPager;
  private MainPagerAdapter mPagerAdapter;
  private ImageView mainMenu;
  private TextView listTitle;
  private View listsNav;
  private ImageView listsNavDisclosure;
  private LinearLayout sidebarLayout;

  private ListView sidebarTaskLists;
  private TextView addListButton;
  private StatisticView statisticView;

  private MainMenuPopover mainMenuPopover;

  // Actionbar set up
  private boolean useLogo = false;
  private boolean showHomeUp = false;
  private boolean isShowingSidebar = false;
  private List<Fragment> fragments;

  public void updateData() {
    ((TaskManageFragment) (fragments.get(0))).updateList();
    updateSidebarData();
  }

  public List<Fragment> getFragments() {
    return fragments;
  }
  public MainPagerAdapter getPagerAdapter() {
	return mPagerAdapter;
  }

  /**
   * Called when the activity is first created Initialize Environment
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.pagerlayout); // Basically a easy linear layout

    // ****************************************************
    // ************Initialize the action bar here**********
    // ****************************************************
    final ActionBar ab = getSupportActionBar();
    ab.setDisplayOptions(0, ActionBar.DISPLAY_SHOW_TITLE);
    ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
    ab.setCustomView(R.layout.header_actionbar_layout);

    // Main menu button setting up
    mainMenu = (ImageView) ab.getCustomView().findViewById(R.id.main_menu);
    mainMenu.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent iPrefs = new Intent("hkust.comp3111h.focus.Activity.PREFSACTIVITY");
        startActivity(iPrefs);
        /*
        mainMenu.setSelected(true);
        mainMenuPopover.show(v);
        */
      }
    });

    // List title, showing the active list
    listsNavDisclosure = (ImageView) ab.getCustomView().findViewById(
        R.id.list_disclosure_arrow);
    listTitle = (TextView) ab.getCustomView().findViewById(R.id.list_title);
    listsNav = ab.getCustomView().findViewById(R.id.taskLists);
    listsNav.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        if (!isShowingSidebar) {
          setListsSidebarSelected(true);
          sidebarLayout.setVisibility(View.VISIBLE);
        } else {
          setListsSidebarSelected(false);
          sidebarLayout.setVisibility(View.GONE);
        }
      }
    });

    // ***************************************************************
    // ******end of action bar
    // **************************************************************
    // Initialise ViewPager
    this.initialiseViewPager();
    initialiseTaskListSidebar();
    createMainMenuPopover();
  }

  private void setListsSidebarSelected(boolean selected) {
    isShowingSidebar = selected;
    int oldTextColor = listTitle.getTextColors().getDefaultColor();
    int textStyle = (selected ? R.style.TextAppearance_ActionBar_ListsHeader_Selected
        : R.style.TextAppearance_ActionBar_ListsHeader);
    listTitle.setTextAppearance(this, textStyle);
    listsNav.setBackgroundColor(selected ? oldTextColor
        : android.R.color.transparent);
    listsNavDisclosure.setSelected(selected);
  }

  private void updateSidebarData() {
    ArrayList<TaskListItem> tlists = getDbAdapter().fetchAllTaskListsObjs(true);
    sidebarTaskLists.setAdapter(new TaskListSidebarAdapter(this,
        sidebarTaskLists, tlists, R.layout.tlist_sidebar_row_layout));
  }

  private void initialiseTaskListSidebar() {
    sidebarLayout = (LinearLayout) findViewById(R.id.sidebar);
    sidebarTaskLists = (ListView) findViewById(R.id.task_lists);
    updateSidebarData();
    Log.d("MainActivity", ""+statisticView);
    sidebarTaskLists.setOnItemClickListener(new OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position,
          long id) {
        if(mPagerAdapter.getItem(2)!=null && mPagerAdapter.getItem(2).getView()!=null&&statisticView==null) {
          statisticView = (StatisticView)mPagerAdapter.getItem(2).getView().findViewById(R.id.statisview);
        }
        TaskListSidebarAdapter.ViewHolder holder = (TaskListSidebarAdapter.ViewHolder) view
            .getTag();
        if (position == 0) {
          showAddListDialog();
        }
        if (position == 1) {
          listTitle.setText("All");
          ((TaskManageFragment) (fragments.get(0))).setActiveTaskList(null);
          if(statisticView!=null) {
            Log.d("MainActivity",""+statisticView);
            statisticView.setCursor(0);
            statisticView.invalidate();
          }
        }
        if (position >= 2) {
          listTitle.setText(holder.item.taskListName());
          ((TaskManageFragment) (fragments.get(0)))
              .setActiveTaskList(holder.item);
          Log.d("MainActivity", "Clicked item is " + holder.item.toString());
          if(statisticView!=null) {
            statisticView.setCursor(holder.item.taskListId());
            statisticView.invalidate();
            Log.d("MainActivity",""+statisticView);
          }
        }
        ((TaskManageFragment) fragments.get(0)).updateList();
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
    this.mPagerAdapter = new MainPagerAdapter(
        super.getSupportFragmentManager(), fragments);
    this.mViewPager = (ViewPager) super.findViewById(R.id.viewpager);
    this.mViewPager.setAdapter(this.mPagerAdapter);
    // Bind the title indicator to the adapter
    TitlePageIndicator titleIndicator = (TitlePageIndicator) findViewById(R.id.titleIndicator);
    titleIndicator.setViewPager(this.mViewPager);
    titleIndicator.setOnPageChangeListener(this);
  }

  @Override
  protected Dialog onCreateDialog(int id) {
    Dialog dialog;
    switch (id) {
      case DIALOG_QUICK_ADD:
        dialog = new QuickAddDialog(this, (TaskManageFragment) fragments.get(0));
        break;
      case DIALOG_ADD_TLIST:
        dialog = new AddTaskListDialog(this,
            (TaskManageFragment) fragments.get(0));
        break;
      default:
        dialog = null;
    }
    return dialog;
  }

  public void showQuickAddDialog() {
    showDialog(DIALOG_QUICK_ADD);
  }

  public void showAddListDialog() {
    showDialog(DIALOG_ADD_TLIST);
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    Log.v("Activity result", "requestCode is" + requestCode);
    if (requestCode == TaskManageFragment.ACTIVITY_EDIT_TASK
        && resultCode != RESULT_CANCELED) {

    }
    ((TaskManageFragment) mPagerAdapter.getItem(0)).updateList();
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
  protected void onPause() {
    super.onPause();
    if (mainMenuPopover != null) {
      mainMenuPopover.dismiss();
    }
  }

  @Override
  public void onPageSelected(int position) {
    // TODO Auto-generated method stub
    if (position == 1) {
      Log.d("MainActivity", "ViewPager Changed to" + position);
      ((TimerFragment) (fragments.get(1))).updateWheelData();
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    super.onCreateOptionsMenu(menu);
    MenuInflater mInflater = getMenuInflater();
    mInflater.inflate(R.menu.general_menu, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.aboutUs:
        Intent iAbout = new Intent("hkust.comp3111h.focus.Activity.ABOUTACTIVITY");
        startActivity(iAbout);
        break;
      case R.id.preference:
        Intent iPrefs = new Intent("hkust.comp3111h.focus.Activity.PREFSACTIVITY");
        startActivity(iPrefs);
        break;
    }
    return true;
  }

}
