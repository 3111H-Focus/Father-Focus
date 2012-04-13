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
import hkust.comp3111h.focus.Adapter.TaskListSidebarAdapter;
import hkust.comp3111h.focus.Adapter.TaskDnDAdapter;
import hkust.comp3111h.focus.ui.MainMenuPopover;
import hkust.comp3111h.focus.ui.MainMenuPopover.MainMenuListener;
import hkust.comp3111h.focus.ui.StatisticsFragment;
import hkust.comp3111h.focus.ui.TaskManageFragment;
import hkust.comp3111h.focus.ui.TimerFragment;
import hkust.comp3111h.focus.ui.TitlePageIndicator;
import hkust.comp3111h.focus.ui.FragmentPopover;
import hkust.comp3111h.focus.database.TaskListItem;
import hkust.comp3111h.focus.database.TaskItem;
import hkust.comp3111h.focus.ui.AddTaskListDialog;

import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;


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
import android.support.v4.app.FragmentManager;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;
import android.support.v4.view.MenuItem.OnMenuItemClickListener;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

public class MainActivity extends FragmentActivity implements
    ActionBar.TabListener, ViewPager.OnPageChangeListener, MainMenuListener{
  public static final String BROADCAST_REQUEST_EVENT_REFRESH = "hkust.comp3111h.focus.REQUEST_EVENT_REFRESH";
  // determine honecome or not
  static final int DIALOG_QUICK_ADD = 0;
  static final int DIALOG_ADD_TLIST = 1;


  private final Handler handler = new Handler();
  private ViewPager mViewPager;
  private PagerAdapter mPagerAdapter;
  private ImageView mainMenu;
  private TextView listTitle;
  private View listsNav;
  private ImageView listsNavDisclosure;
  private LinearLayout sidebarLayout;

  private ListView sidebarTaskLists;
  private TextView addListButton;

  private MainMenuPopover mainMenuPopover;

  // Actionbar set up
  private boolean useLogo = false;
  private boolean showHomeUp = false;
  private boolean isShowingSidebar = false;
  private TaskDbAdapter mDbAdapter;
  private List<Fragment> fragments;

  public void updateData() {
    ((TaskManageFragment)(fragments.get(0))).updateList();
    updateSidebarData();
  }

  public List<Fragment> getFragments() {
    return fragments;
  }


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
        if(!isShowingSidebar) {
          setListsSidebarSelected(true);
          sidebarLayout.setVisibility(View.VISIBLE);
        } else {
          setListsSidebarSelected(false);
          sidebarLayout.setVisibility(View.GONE);
        }
      }
    });

    //***************************************************************
    //******end of action bar
    //**************************************************************
    mDbAdapter = new TaskDbAdapter(this);
    mDbAdapter.open();
    // Initialise ViewPager
    this.initialiseViewPager();
    initialiseTaskListSidebar();
    createMainMenuPopover();
  }

  private void setListsSidebarSelected(boolean selected) {
    isShowingSidebar = selected;
    int oldTextColor = listTitle.getTextColors().getDefaultColor();
    int textStyle = (selected ? R.style.TextAppearance_ActionBar_ListsHeader_Selected:R.style.TextAppearance_ActionBar_ListsHeader);
    listTitle.setTextAppearance(this, textStyle);
    listsNav.setBackgroundColor(selected ? oldTextColor: android.R.color.transparent);
    listsNavDisclosure.setSelected(selected);
  }
  private void updateSidebarData() {
    ArrayList<TaskListItem> tlists = getDbAdapter().fetchAllTaskListsObjs(true);
    sidebarTaskLists.setAdapter(new TaskListSidebarAdapter(
          this,
          sidebarTaskLists,
          tlists,
          R.layout.tlist_sidebar_row_layout));
  }

  private void initialiseTaskListSidebar() {
    sidebarLayout = (LinearLayout) findViewById(R.id.sidebar);
    sidebarTaskLists = (ListView) findViewById(R.id.task_lists);
    updateSidebarData();
    sidebarTaskLists.setOnItemClickListener(new OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view,
        int position, long id) {
        TaskListSidebarAdapter.ViewHolder holder =(TaskListSidebarAdapter.ViewHolder) view.getTag();
        if(position == 0) {
          showAddListDialog();
        }
        if(position == 1) {
          listTitle.setText("All");
          ((TaskManageFragment)(fragments.get(0))).setActiveTaskList(null);
        }
        if(position >= 2) {
          listTitle.setText(holder.item.taskListName());
          ((TaskManageFragment)(fragments.get(0))).setActiveTaskList(holder.item);
          Log.d("MainActivity", "Clicked item is "+ holder.item.toString());
        }
        ((TaskManageFragment)fragments.get(0)).updateList();
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
      case DIALOG_ADD_TLIST:
        dialog = new AddTaskListDialog(this,(TaskManageFragment)fragments.get(0));
        break;
      default:
        dialog=null;
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
  private void setTasksByList(TaskListItem item) {

  }

  /**
   * General function to setup the popover
   */
  private void setupPopoverWithFragment(
      FragmentPopover popover,
      Fragment frag, 
      LayoutParams params) {
    if(popover != null) {
      Log.d("MainAcitivity","Fragment is "+frag.toString());
      View view = frag.getView();
      Log.d("MainActivity","The view:"+view);
      if(view!=null) {
        FrameLayout parent = (FrameLayout) view.getParent();
        if(parent!=null) {
          parent.removeView(view);
        }
        if(params == null) {
          popover.setContent(view);
        }else {
          Log.d("MainActivity", "Setting content");
          popover.setContent(view,params);
        }
      }
    }
  }

  protected Fragment setupFragment(String tag, int container, Class<? extends Fragment> cls) {
    FragmentManager fm = getSupportFragmentManager();
    Fragment fragment = fm.findFragmentByTag(tag);
    if(fragment == null) {
      try{
        fragment = cls.newInstance();
      } catch (InstantiationException e) {
        return null;
      } catch (IllegalAccessException e) {
        return null;
      }

      FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
      if(container==0) {
        ft.add(fragment, tag);
      } else {
        ft.replace(container, fragment, tag);
      }
      ft.commit();
      
    }
    return fragment;
  }

  private void setTaskByList(TaskListItem item) {

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
