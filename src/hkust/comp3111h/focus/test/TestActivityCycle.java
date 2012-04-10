package hkust.comp3111h.focus.test;

import hkust.comp3111h.focus.R;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class TestActivityCycle extends Activity implements OnClickListener{
  private static final String TAG = "Test Activity Cycle";
  private Button bConfirm;
  private Button bCancel;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    // TODO Auto-generated method stub
    super.onCreate(savedInstanceState);
    Log.d(TAG, "onCreate");
    setContentView(R.layout.test_activity_cycle);
    bConfirm = (Button) findViewById(R.id.bConfirm);
    bCancel = (Button) findViewById(R.id.bCancel);
    
    bConfirm.setOnClickListener(this);
    bCancel.setOnClickListener(this);
  }
  
  @Override
  protected void onStart() {
    // TODO Auto-generated method stub
    super.onStart();
    Log.d(TAG, "onStart");
  }


  @Override
  protected void onResume() {
    // TODO Auto-generated method stub
    super.onResume();
    Log.d(TAG, "onResume");
  }

  @Override
  protected void onPause() {
    // TODO Auto-generated method stub
    super.onPause();
    Log.d(TAG, "onPause");
  }
  
  @Override
  protected void onStop() {
    // TODO Auto-generated method stub
    super.onStop();
    Log.d(TAG, "onStop");
  }
  
  @Override
  protected void onDestroy() {
    // TODO Auto-generated method stub
    super.onDestroy();
    Log.d(TAG, "onDestroy");
  }

  @Override
  protected void onRestart() {
    // TODO Auto-generated method stub
    super.onRestart();
    Log.d(TAG, "onRestart");
  }

  @Override
  public void onClick(View v) {
    // TODO Auto-generated method stub
    switch(v.getId()){
      case R.id.bConfirm:
        break;
      case R.id.bCancel:
        break;
    }
    
  }
}
