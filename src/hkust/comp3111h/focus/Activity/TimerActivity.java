package hkust.comp3111h.focus.Activity;

import hkust.comp3111h.focus.R;
import hkust.comp3111h.focus.ui.StatisticsFragment;
import hkust.comp3111h.focus.ui.TaskManageFragment;
import hkust.comp3111h.focus.ui.TimerFragment;
import android.animation.ObjectAnimator;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import android.support.v4.app.ActionBar;
import android.support.v4.app.ActionBar.OnNavigationListener;
import android.support.v4.app.ActionBar.Tab;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;
import android.support.v4.view.MenuItem.OnMenuItemClickListener;
import android.support.v4.view.ViewPager;

import android.view.View;

import android.view.animation.AlphaAnimation;
import android.view.animation.RotateAnimation;
import android.widget.ArrayAdapter;

import android.content.Intent;
import android.content.Context;

public class TimerActivity extends FragmentActivity {
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.timer_layout); // Basically a easy linear layout
  }
}
