package hkust.comp3111h.focus.ui;

import java.util.ArrayList;
import java.util.Date;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ToggleButton;

import hkust.comp3111h.focus.R;
import hkust.comp3111h.focus.ui.CalendarView.OnSelectedDateListener;

public class DateAndTimePicker extends LinearLayout {
  public interface OnDateChangedListener {
    public void onDateChanged();
  }

  private final int SHORTCUT_PADDING = 8;
  ArrayList<ShortcutValue> shortcutValues;

  private class ShortcutValue {
    public String label;
    public DateTime dueDate;
    public ShortcutValue(String label,DateTime date) {
       this.label = label;
       dueDate = date;
    }
    @Override
    public String toString() {
      return label;
    }
  }


  private final CalendarView calendarView;
  private final LinearLayout dateShortcuts;
  private OnDateChangedListener listener;

  public DateAndTimePicker(Context context, AttributeSet attrs) {
    super(context, attrs);
    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    inflater.inflate(R.layout.date_time_picker_layout, this, true);
    calendarView = (CalendarView) findViewById(R.id.calendar);
    dateShortcuts = (LinearLayout) findViewById(R.id.date_shortcuts);
    setUpListeners();
    constructShortcutList(context);
  }

  public void initializeWithDate(String dateValue) {
    DateTime date;
    Date calendarDate;
    if(dateValue!=null&&dateValue.length()!=0) {
      date = DateTime.parse(dateValue);
      calendarDate = date.toDate();
    } else {
      date = null;
      calendarDate = new Date(0);
    }
    calendarView.setCalendarDate(calendarDate);
    updateShortcutView(date);
  }

  private void setUpListeners() {
    calendarView.setOnSelectedDateListener(new OnSelectedDateListener() {
      @Override
      public void onSelectedDate(Date date) {
        updateShortcutView(new DateTime(date.getTime()));
        extraCallbacks();
      }
    });
  }

  private void constructShortcutList(Context context) {
    String[] labels = context.getResources().getStringArray(R.array.shortcut_labels);
    shortcutValues = new ArrayList<ShortcutValue>();
    shortcutValues.add(new ShortcutValue(labels[1], new DateTime())); //Today
    shortcutValues.add(new ShortcutValue(labels[2], new DateTime().plusDays(1))); //Tomorrow
    shortcutValues.add(new ShortcutValue(labels[3], new DateTime().plusDays(7))); //next week
    shortcutValues.add(new ShortcutValue(labels[4], new DateTime().plusMonths(1))); //next month
    shortcutValues.add(new ShortcutValue(labels[0], null)); //no deadline
    Resources r = context.getResources();
    DisplayMetrics metrics = r.getDisplayMetrics();
    //Define the appearence of the (toggle) button
    int onColorValue = r.getColor(R.color.theme_blue_light);
    int offColorValue = r.getColor(android.R.color.transparent);
    int borderColorValue = r.getColor(R.color.task_edit_deadline_gray);
    int cornerRadius = (int) (5*r.getDisplayMetrics().density);
    int strokeWidth = (int)(2*r.getDisplayMetrics().density);

    for(int i = 0; i< shortcutValues.size(); i++) {
      LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, (int) (42 * metrics.density),0);
      ShortcutValue uv = shortcutValues.get(i);

      ToggleButton tb = new ToggleButton(context);
      String label = uv.label;
      tb.setTextOff(label);
      tb.setTextOn(label);
      tb.setTag(uv); //For more efficient accessing

      if(i==0) {
        tb.setBackgroundDrawable(CustomBorderDrawable.customButton(cornerRadius, cornerRadius, 0, 0, onColorValue, offColorValue, borderColorValue, strokeWidth));
      }else if(i==shortcutValues.size()-2){
        lp.topMargin = (int) (-2*metrics.density);
          tb.setBackgroundDrawable(CustomBorderDrawable.customButton(0, 0, cornerRadius, cornerRadius, onColorValue, offColorValue, borderColorValue, strokeWidth));
      }else if(i==shortcutValues.size()-1) {
        lp.topMargin = (int) (5* metrics.density);
        tb.setBackgroundDrawable(CustomBorderDrawable.customButton(cornerRadius,cornerRadius, cornerRadius, cornerRadius, onColorValue, offColorValue, borderColorValue, strokeWidth));
      }else {
        lp.topMargin = (int) (-2*metrics.density);
          tb.setBackgroundDrawable(CustomBorderDrawable.customButton(0,0,0,0, onColorValue, offColorValue, borderColorValue, strokeWidth));
      }
      int verticalPadding = (int) (SHORTCUT_PADDING * metrics.density);
      tb.setPadding(0, verticalPadding, 0, verticalPadding);
      tb.setLayoutParams(lp);
      tb.setGravity(Gravity.CENTER);
      tb.setTextSize(18);
      tb.setTextColor(context.getResources().getColorStateList(R.color.toggle_button_text_color));
      tb.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          ShortcutValue value = (ShortcutValue) v.getTag();
          DateTime date;
          if(value.dueDate!=null) {
            date = new DateTime(value.dueDate);
            calendarView.setCalendarDate(date.toDate());
          }else {
            date = null;
            calendarView.setCalendarDate(new Date(0));
          }
          calendarView.invalidate();
          updateShortcutView(date);
          extraCallbacks();
        }
      });
      dateShortcuts.addView(tb);
    }
  }
  private void updateShortcutView(DateTime date) {
    ToggleButton tb_no_due = (ToggleButton) dateShortcuts.getChildAt(dateShortcuts.getChildCount()-1);
    if(date==null||date.getMillis()<=0) {
      tb_no_due.setChecked(true);
    }else {
      tb_no_due.setChecked(false);
    }
    for(int i=0; i< dateShortcuts.getChildCount()-1;i++) {
      ToggleButton tb = (ToggleButton) dateShortcuts.getChildAt(i);
      ShortcutValue sv = (ShortcutValue) tb.getTag();
      if(sv.dueDate!=null&&date!=null&&
          sv.dueDate.getMillis()/86400000 == date.getMillis()/86400000) {
        tb.setChecked(true);
        tb_no_due.setChecked(false);
      }else {
        tb.setChecked(false);
      }
    }
  }
  private void extraCallbacks() {
    if(listener!=null) {
      listener.onDateChanged();
    }
  }

  public DateTime constructDueDate() {
    DateTime calendarDate = new DateTime(calendarView.getCalendarDate().getTime());
    return calendarDate;
  }

  public void setOnDateChangedListener(OnDateChangedListener listener) {
    this.listener = listener;
  }
  public String getDisplayString(Context context, boolean useNewLine, boolean hideYear) {
    DateTime dueDate = constructDueDate();
    return getDisplayString(context, dueDate, useNewLine, hideYear);
  }
  public static String getDisplayString(Context context, DateTime dueDate,  boolean useNewLine, boolean hideYear) {
    if(dueDate.getMillis()<=0) {
      return "";
    }
    DateTimeFormatterBuilder formatterBuilder = new DateTimeFormatterBuilder().appendMonthOfYearText().appendLiteral(' ').appendDayOfMonth(2);
    if(useNewLine) {
      formatterBuilder.appendLiteral('\n');
    } else {
      formatterBuilder.appendLiteral(' ');
    }
    if(!hideYear) {
      formatterBuilder.appendYear(4,4);
    }
    DateTimeFormatter formatter = formatterBuilder.toFormatter();
    return dueDate.toString(formatter);
  }
}

