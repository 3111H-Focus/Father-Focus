package hkust.comp3111h.focus.Activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.SupportActivity;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;
import android.support.v4.view.ViewPager;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.widget.TextView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import hkust.comp3111h.focus.ui.TabPageIndicator;

import hkust.comp3111h.focus.R;

public class EditTaskActivity extends FocusBaseActivity {
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.task_edit_activity_layout);

    final ActionBar actionBar = getSupportActionBar();
    actionBar.setDisplayHomeAsUpEnabled(true);
    actionBar.setDisplayShowTitleEnabled(false);

    actionBar.setDisplayShowCustomEnabled(true);
    actionBar.setCustomView(R.layout.actionbar_title_layout);
    ((TextView) actionBar.getCustomView().findViewById(R.id.title)).
      setText(R.string.task_edit);
  }
  public void updateTitle(boolean isNewTask) {
    ActionBar actionBar = getSupportActionBar();
    if(actionBar!=null) {
      ((TextView) actionBar.getCustomView().findViewById(R.id.title)).
        setText(isNewTask?R.string.TE_new_task:R.string.TE_edit_task);
    }
  }
  @Override
  protected void onResume() {
    super.onResume();
    Fragment frag = (Fragment) getSupportFragmentManager().findFragmentByTag(EditTaskFragment.TAG_EDITTASK_FRAGMENT);
  }
}
