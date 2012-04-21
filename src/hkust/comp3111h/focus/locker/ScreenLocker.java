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

/**
 * The ScreenLocker class used to lock the screen
 * @author Gary Cheung
 * 1. Use one of the constructors to initiate the ScreenLocker
 * 2. Use lock() to start the locking actions
 * 3. Use cancel_lock() to cancel the locking actions
 * 4. Use set_popup_description() to set the description in the pop-up window
 * 5. Use set_candidate_strings() to set the candidate strings
 * 6. Use change_to_all_random_generation() to change the method to 
 *    all-random generation and make the candidate strings null
 */

public class ScreenLocker {
  private static final int DEFAULT_LOCK_DOWN_COUNT_TIME = 10000;
  private static final int DEFAULT_WAIT_FOR_INPUT_TIME = 5000;
  private static final int DEFAULT_RETYPE_LENGTH = 10;
  private DevicePolicyManager dpm;
  private ComponentName componentName;
  private Activity activity;
  private CountDownTimer lock_count_down;
  private int lock_count_down_time;
  private CountDownTimer wait_for_input;
  private int wait_for_input_time;
  private PopupWindow popup_window;
  private View popup_view;
  private TextView popup_description;
  private TextView popup_relock;
  private TextView popup_sequence;
  private EditText popup_input;
  private Button lazy_button;
  private Button gary_lee_button;
  private Random string_generater;
  private String[] candidate_strings;
  private int retype_length;
	
  /**
   * The basic constructor
   * @param activity: the activity this locker bases on
   */
  public ScreenLocker(Activity activity) {
    this.activity = activity;
    this.wait_for_input_time = DEFAULT_WAIT_FOR_INPUT_TIME;
    this.lock_count_down_time = DEFAULT_LOCK_DOWN_COUNT_TIME;
    this.retype_length = DEFAULT_RETYPE_LENGTH;
	dpm  = (DevicePolicyManager)this.activity.getSystemService(Context.DEVICE_POLICY_SERVICE);
   	componentName = new ComponentName(this.activity, AdminReceiver.class);
   	string_generater = new Random();
   	popup_init();
   	timers_init();
   	listeners_init();
  }
  
  /**
   * Constructor
   * @param activity: the activity this locker bases on
   * @param lock_count_down_time: how many milliseconds to wait before locking
   * @param wait_for_input_time: how many milliseconds to wait after some input before starting to wait for locking
   */
  public ScreenLocker(Activity activity, int lock_count_down_time, int wait_for_input_time) {
	this.activity = activity;
	this.wait_for_input_time = wait_for_input_time;
	this.lock_count_down_time = lock_count_down_time;
	this.retype_length = DEFAULT_RETYPE_LENGTH;
	dpm  = (DevicePolicyManager)this.activity.getSystemService(Context.DEVICE_POLICY_SERVICE);
	componentName = new ComponentName(this.activity, AdminReceiver.class);
   	string_generater = new Random();
   	popup_init();
   	timers_init();
   	listeners_init();
  }
  
  /**
   * Constructor
   * @param activity: the activity this locker bases on
   * @param retype_length: the length of the sequence needed to retype 
   */
  public ScreenLocker(Activity activity, int retype_length) {
	this.activity = activity;
	this.wait_for_input_time = DEFAULT_WAIT_FOR_INPUT_TIME;
    this.lock_count_down_time = DEFAULT_LOCK_DOWN_COUNT_TIME;
    this.retype_length = retype_length;
	dpm  = (DevicePolicyManager)this.activity.getSystemService(Context.DEVICE_POLICY_SERVICE);
	componentName = new ComponentName(this.activity, AdminReceiver.class);
   	string_generater = new Random();
   	popup_init();
   	timers_init();
   	listeners_init();
  }
  
  /**
   * Constructor
   * Be aware that once the candidate_strings is given a non-null value,
   * the string generated for output will be picked in the set
   * @param activity: the activity this locker bases on
   * @param candidate_strings: the strings as candidates for unlocking retype 
   */
  public ScreenLocker(Activity activity, String[] candidate_strings) {
	this.activity = activity;
	this.wait_for_input_time = DEFAULT_WAIT_FOR_INPUT_TIME;
    this.lock_count_down_time = DEFAULT_LOCK_DOWN_COUNT_TIME;
    this.retype_length = DEFAULT_RETYPE_LENGTH;
    this.candidate_strings = candidate_strings;
	dpm  = (DevicePolicyManager)this.activity.getSystemService(Context.DEVICE_POLICY_SERVICE);
	componentName = new ComponentName(this.activity, AdminReceiver.class);
   	string_generater = new Random();
   	popup_init();
   	timers_init();
   	listeners_init();
  }
  
  /**
   * Constructor
   * Be aware that once the candidate_strings is given a non-null value,
   * the string generated for output will be picked in the set
   * @param activity: the activity this locker bases on
   * @param retype_length: the length of the sequence needed to retype 
   * @param candidate_strings: the strings as candidates for unlocking retype 
   */
  public ScreenLocker(Activity activity, int retype_length, String[] candidate_strings) {
	this.activity = activity;
	this.wait_for_input_time = DEFAULT_WAIT_FOR_INPUT_TIME;
    this.lock_count_down_time = DEFAULT_LOCK_DOWN_COUNT_TIME;
    this.retype_length = retype_length;
    this.candidate_strings = candidate_strings;
	dpm  = (DevicePolicyManager)this.activity.getSystemService(Context.DEVICE_POLICY_SERVICE);
	componentName = new ComponentName(this.activity, AdminReceiver.class);
   	string_generater = new Random();
   	popup_init();
   	timers_init();
   	listeners_init();
  }
  
  /**
   * Constructor
   * Be aware that once the candidate_strings is given a non-null value,
   * the string generated for output will be picked in the set
   * @param activity: the activity this locker bases on
   * @param lock_count_down_time: how many milliseconds to wait before locking
   * @param wait_for_input_time: how many milliseconds to wait after some input before starting to wait for locking
   * @param candidate_strings: the strings as candidates for unlocking retype 
   */
  public ScreenLocker(Activity activity, int lock_count_down_time, int wait_for_input_time, String[] candidate_strings) {
	this.activity = activity;
	this.wait_for_input_time = wait_for_input_time;
	this.lock_count_down_time = lock_count_down_time;
	this.retype_length = DEFAULT_RETYPE_LENGTH;
    this.candidate_strings = candidate_strings;
	dpm  = (DevicePolicyManager)this.activity.getSystemService(Context.DEVICE_POLICY_SERVICE);
	componentName = new ComponentName(this.activity, AdminReceiver.class);
   	string_generater = new Random();
   	popup_init();
   	timers_init();
   	listeners_init();
  }
  
  /**
   * Constructor
   * @param activity: the activity this locker bases on
   * @param lock_count_down_time: how many milliseconds to wait before locking
   * @param wait_for_input_time: how many milliseconds to wait after some input before starting to wait for locking
   * @param retype_length: the length of the sequence needed to retype 
   */
  public ScreenLocker(Activity activity, int lock_count_down_time, int wait_for_input_time, int retype_length) {
	this.activity = activity;
	this.wait_for_input_time = wait_for_input_time;
	this.lock_count_down_time = lock_count_down_time;
	this.retype_length = retype_length;
	dpm  = (DevicePolicyManager)this.activity.getSystemService(Context.DEVICE_POLICY_SERVICE);
	componentName = new ComponentName(this.activity, AdminReceiver.class);
   	string_generater = new Random();
   	popup_init();
   	timers_init();
   	listeners_init();
  }

  /**
   * Constructor
   * Be aware that once the candidate_strings is given a non-null value,
   * the string generated for output will be picked in the set
   * @param activity: the activity this locker bases on
   * @param lock_count_down_time: how many milliseconds to wait before locking
   * @param wait_for_input_time: how many milliseconds to wait after some input before starting to wait for locking
   * @param retype_length: the length of the sequence needed to retype 
   * @param candidate_strings: the strings as candidates for unlocking retype 
   */
  public ScreenLocker(Activity activity, int lock_count_down_time, int wait_for_input_time, int retype_length,
		              String[] candidate_strings) {
	this.activity = activity;
	this.wait_for_input_time = wait_for_input_time;
	this.lock_count_down_time = lock_count_down_time;
	this.retype_length = retype_length;
	this.candidate_strings = candidate_strings;
	dpm  = (DevicePolicyManager)this.activity.getSystemService(Context.DEVICE_POLICY_SERVICE);
	componentName = new ComponentName(this.activity, AdminReceiver.class);
   	string_generater = new Random();
   	popup_init();
   	timers_init();
   	listeners_init();
  }

  public void lock() {
	popup();
	setReceiver();
	lock_count_down.start();
  }

  public void cancel_lock() {
	wait_for_input.cancel();
	lock_count_down.cancel();
	popup_relock.setText("Or it will be locked soon!");
	popup_window.dismiss();
  }
  
  public void set_popup_description(String description) {
	popup_description.setText(description);
	popup_window.update();
  }
  
  public void set_candidate_strings(String[] candidate_strings) {
	this.candidate_strings = candidate_strings;
  }
  
  public void change_to_all_random_generation(int retype_length) {
	this.candidate_strings = null;
	this.retype_length = retype_length;
  }
  
  private void timers_init() {
	lock_count_down = new CountDownTimer(lock_count_down_time, 1000) {
	  @Override
	  public void onTick(long millisUntilFinished) {
		popup_relock.setText("Or it will be locked in "+ Long.toString(millisUntilFinished / 1000) +
					           " seconds!");
		popup_window.update();
	  }
	  @Override
      public void onFinish() {
		popup_relock.setText("Or it will be locked soon!"); 
		popup_window.update();
		sysLock();
	  }
	};
	wait_for_input = new CountDownTimer(wait_for_input_time, 1000) {
	  @Override
      public void onFinish() {
	    lock_count_down.start();
	  }
	  @Override
	  public void onTick(long millisUntilFinished) {
	    // TODO Auto-generated method stub
      } 
	};
  }
  
  private void listeners_init() {
	popup_input.addTextChangedListener(new TextWatcher() {
	  @Override
      public void afterTextChanged(Editable arg0) {
	    lock_count_down.cancel();
	    popup_relock.setText("Or it will be locked soon!");
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
	OnClickListener button_listener = new OnClickListener() {
	  @Override
	  public void onClick(View v) {
 	    switch(v.getId()) {
	   	  case R.id.locker_pop_lazy_button:
	   	  	try_escape();
	       	break;
	      case R.id.locker_pop_gary_lee_button:
   	    	lock_count_down.cancel();
   	    	popup_relock.setText("Or it will be locked soon!");
   	    	sysLock();
   	    	break;
   	    }
	  }
	};
	lazy_button.setOnClickListener(button_listener);
	gary_lee_button.setOnClickListener(button_listener);
  }
  
  private void popup_init() {
	LayoutInflater mLayoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	popup_view = mLayoutInflater.inflate(R.layout.pop_background, null);
	popup_window = new PopupWindow(popup_view, LayoutParams.MATCH_PARENT,
                         LayoutParams.MATCH_PARENT, true);
	popup_window.setAnimationStyle(R.style.PopWindowAnimation);

	popup_description = (TextView)popup_view.findViewById(R.id.locker_pop_description_text);
	popup_relock = (TextView)popup_view.findViewById(R.id.locker_pop_relock_time);
	popup_input = (EditText)popup_view.findViewById(R.id.locker_pop_input);
	popup_sequence = (TextView)popup_view.findViewById(R.id.locker_pop_unlock_sequence);
	popup_sequence.setText(generateSeq());

	lazy_button = (Button)popup_view.findViewById(R.id.locker_pop_lazy_button);
	gary_lee_button = (Button)popup_view.findViewById(R.id.locker_pop_gary_lee_button);
  }
  
  private void popup() {
	LayoutInflater mLayoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    View parent_view = mLayoutInflater.inflate(R.layout.main, null);
	popup_window.showAtLocation(parent_view, Gravity.CENTER | Gravity.CENTER, 0, 0);
  }
  
  private void try_escape() {
	String et_string = popup_input.getText().toString();
	String tv_string = popup_sequence.getText().toString(); 
	if (et_string.equals(tv_string)) {
	  lock_count_down.cancel();
	  popup_window.dismiss();
	} else {
	  popup_sequence.setText(generateSeq());
	  popup_input.setText("");
	  popup_window.update();
	}
  }
  
  private void setReceiver() {
	IntentFilter filter = new IntentFilter(); 
	filter.addAction(Intent.ACTION_USER_PRESENT); 
	final BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {   
	      @Override   
		  public void onReceive(final Context context, final Intent intent) {   
		    String action = intent.getAction();   
		    if(Intent.ACTION_USER_PRESENT.equals(action)) {
		      lock_count_down.start();
		  	  popup_sequence.setText(generateSeq());
		  	  popup_window.update();
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
	if (candidate_strings == null) {
	  return allRandomGenerateSeq();
	} else {
	  return pickCandidateGenerateSeq();
	}
  }
  
  private String pickCandidateGenerateSeq() {
    int g = string_generater.nextInt(candidate_strings.length);
    return candidate_strings[g];
  }
  
  private String allRandomGenerateSeq() {
	char[] result = new char[retype_length];
	for (int word_length = 0; word_length < retype_length; word_length++) {
	  int g = string_generater.nextInt(62);
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
