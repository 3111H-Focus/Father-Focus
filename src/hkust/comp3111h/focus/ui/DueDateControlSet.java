package hkust.comp3111h.focus.ui;

import android.app.Activity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.LayoutInflater;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.util.Log;
import org.joda.time.DateTime;


import hkust.comp3111h.focus.R;
import hkust.comp3111h.focus.util.PopupControlSet;
import hkust.comp3111h.focus.database.TaskItem;
public class DueDateControlSet extends PopupControlSet {
  private DateAndTimePicker dateAndTimePicker;
  public DueDateControlSet(Activity activity, int viewLayout, int displayViewLayout) {
    super(activity, viewLayout, displayViewLayout, 0);
    this.displayText.setText(activity.getString(R.string.TE_when_label));
  }

  /**
   * Setting up the title basically
   */
  @Override 
  protected void refreshDisplayView() {
    StringBuilder displayString = new StringBuilder();
    if(initialized) {
      displayString.append(dateAndTimePicker.getDisplayString(activity,false, false));
    }else{
      if(task.dueDate()!=null) {
        displayString.append(DateAndTimePicker.getDisplayString(activity, DateTime.parse(task.dueDate()),false, false));
      } 
    }
    TextView dateDisplay = (TextView) getDisplayView().findViewById(R.id.display_row_edit);
    dateDisplay.setText(displayString);
  }

  @Override 
  protected void afterInflate() {
    dateAndTimePicker = (DateAndTimePicker) getView().findViewById(R.id.date_and_time);
    Button okButton = (Button) LayoutInflater.from(activity).inflate(R.layout.control_dialog_ok, null);
    okButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        onOkClick();
        DueDateControlSet.this.dialog.dismiss();
      }
    });
    LinearLayout body = (LinearLayout)getView().findViewById(R.id.datetime_body);
    body.addView(okButton);
  }

  @Override
  protected void initTask() {
    String dueDate = task.dueDate();
    Log.v("DueDate", "DueDate is "+dueDate);
    initializeWithDate(dueDate);
    refreshDisplayView();
  }
  /**
   * Write the correspoinding data back
   */
  @Override
  protected String writeDataAfterInit(TaskItem task) {
    String dueDate = dateAndTimePicker.constructDueDate().toString();
    task.dueDate(dueDate);
    return null;
  }

  private void initializeWithDate(String dueDate) {
    dateAndTimePicker.initializeWithDate(dueDate);
  }
  public boolean isDueSet() {
    return (dateAndTimePicker!=null && dateAndTimePicker.constructDueDate()!=null);
  }
}

