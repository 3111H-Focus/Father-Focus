package hkust.comp3111h.focus.ui;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ScreenLocker {
  private DevicePolicyManager dpm;
  private ComponentName componentName;
  private Activity activity;
	
  public ScreenLocker(Activity activity) {
    this.activity = activity;
	dpm  = (DevicePolicyManager)this.activity.getSystemService(Context.DEVICE_POLICY_SERVICE);
   	componentName = new ComponentName(this.activity, AdminReceiver.class);
  }
	
  public void lock() {
	this.sysLock();
	//boolean active = dpm.isAdminActive(componentName);
	//activeManage();
  }

  private void activeManage() {
	  Log.d("going in active", "try");
    Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
    intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
    intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Please confirm");
    Log.d("leaving activie", "");

    activity.startActivityForResult(intent, 0);
    Log.d("leaving activie", "");
  }
  
  private void sysLock(){
    boolean active = dpm.isAdminActive(componentName);
    if (!active) {
      activeManage();
    }
    if (active) {
      dpm.lockNow();
	}
  }
}
