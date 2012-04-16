package hkust.comp3111h.focus.locker;

import hkust.comp3111h.focus.R;
import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.PopupWindow;

public class ScreenLocker {
  private DevicePolicyManager dpm;
  private ComponentName componentName;
  private Activity activity;
  private CountDownTimer cdt;
  private PopupWindow pw;
	
  public ScreenLocker(Activity activity) {
    this.activity = activity;
	dpm  = (DevicePolicyManager)this.activity.getSystemService(Context.DEVICE_POLICY_SERVICE);
   	componentName = new ComponentName(this.activity, AdminReceiver.class);
  }
  
  private void popup() {
	LayoutInflater mLayoutInflater = (LayoutInflater) activity.getSystemService(activity.LAYOUT_INFLATER_SERVICE);
	View popview = mLayoutInflater.inflate(R.layout.pop_background, null);
	pw = new PopupWindow(popview, LayoutParams.MATCH_PARENT,
                         LayoutParams.MATCH_PARENT, true);
	pw.setAnimationStyle(R.style.PopWindowAnimation);
	View p = mLayoutInflater.inflate(R.layout.main, null);
	pw.showAtLocation(p, Gravity.CENTER | Gravity.CENTER, 0, 0);
	
	Button good_button = (Button)popview.findViewById(R.id.good_button);
	OnClickListener listener = new OnClickListener() {
	  @Override
	  public void onClick(View v) {
   	    pw.dismiss();
	  }
	};
	good_button.setOnClickListener(listener);
	
	
  }
  
  public void lock() {
	popup();
	cdt = new CountDownTimer(10000, 1000) {
	  public void onTick(long millisUntilFinished) {
		
      }

	  public void onFinish() {
	    sysLock();
	  }
	};
	cdt.start();
  }

  private void activeManage() {
    Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
    intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
    intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Please confirm");
    activity.startActivityForResult(intent, 0);
  }
  
  private void sysLock(){
    boolean active = dpm.isAdminActive(componentName);
    if (!active) {
      activeManage();
      dpm.lockNow();
    }
    if (active) {
      dpm.lockNow();
	}
  }
}
