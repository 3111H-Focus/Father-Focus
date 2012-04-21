/**
 * This class is the base class of all the activity of this application
 * This is for the reduction of duplication of code
 */
package hkust.comp3111h.focus.Activity;
import hkust.comp3111h.focus.ui.QuickAddDialog;


import hkust.comp3111h.focus.R;
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

public class FocusBaseActivity extends FragmentActivity implements
     TaskManageFragment.OnTaskListItemClickedListener,MainMenuListener,
     ViewPager.OnPageChangeListener
{
  private TaskDbAdapter mDbAdapter;
  public TaskDbAdapter getDbAdapter() {
    return mDbAdapter;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mDbAdapter = new TaskDbAdapter(this);
    mDbAdapter.open();
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
  //Life cycle handling 
  

  @Override
  public void mainMenuItemSelected(int item, Intent customIntent) {
    // TODO implmenent the main menu
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

  public void onPageScrolled(int position, float positionOffset,
      int positionOffsetPixels) {
    // TODO Auto-generated method stub
  }

  public void onPageSelected(int position) {
  }

  public void onPageScrollStateChanged(int state) {
    // TODO Auto-generated method stub
  }

  @Override 
  public void onTaskListItemClicked(long taskId) {
    Intent intent = new Intent(this, EditTaskActivity.class);
    intent.putExtra("id", taskId);
    //Needs to be in activity intent so that TaskEditActivity on Resume
    //doesn't create a blank activity
    getIntent().putExtra(EditTaskFragment.TOKEN_ID, taskId);
    startActivityForResult(intent, TaskManageFragment.ACTIVITY_EDIT_TASK);
  }
}
