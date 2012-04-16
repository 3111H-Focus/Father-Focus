package hkust.comp3111h.focus.locker;

import hkust.comp3111h.focus.R;

import java.util.Random;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

public class ScreenLocker {
  private DevicePolicyManager dpm;
  private ComponentName componentName;
  private Activity activity;
  private CountDownTimer cdt;
  private CountDownTimer wait_for_input;
  private PopupWindow pw;
  View popview;
  TextView pop_relock;
  EditText et;
  TextView tv;
  Random r;
	
  public ScreenLocker(Activity activity) {
    this.activity = activity;
	dpm  = (DevicePolicyManager)this.activity.getSystemService(Context.DEVICE_POLICY_SERVICE);
   	componentName = new ComponentName(this.activity, AdminReceiver.class);
   	r = new Random();
  }
  
  private void popup() {
	LayoutInflater mLayoutInflater = (LayoutInflater) activity.getSystemService(activity.LAYOUT_INFLATER_SERVICE);
	popview = mLayoutInflater.inflate(R.layout.pop_background, null);
	pw = new PopupWindow(popview, LayoutParams.MATCH_PARENT,
                         LayoutParams.MATCH_PARENT, true);
	pw.setAnimationStyle(R.style.PopWindowAnimation);
	View p = mLayoutInflater.inflate(R.layout.main, null);
	pw.showAtLocation(p, Gravity.CENTER | Gravity.CENTER, 0, 0);

	pop_relock = (TextView)popview.findViewById(R.id.relock_time);
	
	et = (EditText)popview.findViewById(R.id.pop_input);
	tv = (TextView)popview.findViewById(R.id.unlock_sequence);
	tv.setText(generateSeq());
	pw.update();
	et.addTextChangedListener(new TextWatcher() {

		@Override
		public void afterTextChanged(Editable arg0) {
		  cdt.cancel();
		  pop_relock.setText("Or it will be locked soon!");
		  wait_for_input.cancel();
		  wait_for_input.start();
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			// TODO Auto-generated method stub
			
		}
		
	});
	
	Button lazy_button = (Button)popview.findViewById(R.id.lazy_button);
	Button gary_lee_button = (Button)popview.findViewById(R.id.gary_lee_button);
	OnClickListener listener = new OnClickListener() {
	  @Override
	  public void onClick(View v) {
   	    switch(v.getId()) {
   	      case R.id.lazy_button:
   	    	try_escape();
   	    	break;
   	      case R.id.gary_lee_button:
   	    	cdt.cancel();
   	    	pop_relock.setText("Or it will be locked soon!");
   	    	sysLock();
   	    	break;
   	    }
	  }
	};
	lazy_button.setOnClickListener(listener);
	gary_lee_button.setOnClickListener(listener);
  }
  
  private void try_escape() {
	String et_string = et.getText().toString();
	String tv_string = tv.getText().toString(); 
	if (et_string.equals(tv_string)) {
	  pw.dismiss();
	  cdt.cancel();
	} else {
      et.setText("");
	}
  }
  
  public void lock() {
	popup();
	setReceiver();
	cdt = new CountDownTimer(10000, 1000) {
	  public void onTick(long millisUntilFinished) {
		pop_relock.setText("Or it will be locked in "+ Long.toString(millisUntilFinished / 1000) +
				           " seconds!");
		pw.update();
      }

	  public void onFinish() {
		pop_relock.setText("Or it will be locked soon!"); 
		pw.update();
	    sysLock();
	  }
	};
	wait_for_input = new CountDownTimer(5000, 1000) {

	  @Override
      public void onFinish() {
	    cdt.start();
	  }
	  @Override
	  public void onTick(long millisUntilFinished) {
		// TODO Auto-generated method stub
	  } 
	};
	cdt.start();
  }

  private void setReceiver() {
	IntentFilter filter = new IntentFilter(); 
	filter.addAction(Intent.ACTION_USER_PRESENT); 
	final BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {   
	      @Override   
		  public void onReceive(final Context context, final Intent intent) {   
		    String action = intent.getAction();   
		    if(Intent.ACTION_USER_PRESENT.equals(action)) {
		      cdt.start();
		  	  tv.setText(generateSeq());
		  	  pw.update();
		    }
	      }
		};
	activity.registerReceiver(mBatInfoReceiver, filter);
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
  
  private String generateSeq() {
	char[] result = new char[10];
	for (int word_length = 0; word_length < 10; word_length++) {
	  int g = r.nextInt(62);
	  if (g < 10) {
	    result[word_length] = (char) (g + 48);
	  } else if (g < 36) {
		result[word_length] = (char) (g + 55);
	  } else if (g < 62) {
		result[word_length] = (char) (g + 61);
	  }
	}
	return new String(result);
  }
}
