package hkust.comp3111h.focus.ui;

import hkust.comp3111h.focus.R;
import hkust.comp3111h.focus.Activity.MainActivity;
import hkust.comp3111h.focus.database.TaskDbAdapter;

import java.util.Random;
import java.util.Vector;

import org.joda.time.DateTime;
import org.joda.time.Duration;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

public class StatisticView extends View {
  private final int WIDTHOFDARKSTROKE = 5; 
  private final float WARNINGANGLE = 10;
	
  private Paint mPaint;
  private Paint strokePaint;
  private Cursor cursor;
  private boolean cursor_is_all;
  private DateTime first_start_date;
  private DateTime last_start_date;
  private boolean first_start_date_set;
  private boolean last_start_date_set;
  private Random random;
  private TaskDbAdapter db;
  private Duration sum_of_durations;
  private float start_angle;
  float last_x = 0;
  float last_y = 0;
  
  private class InformationPair {
	public int color;
	public long task_id;
	public Duration duration;
	public float start_angle;
	public float end_angle;
	
	
	public InformationPair(int color, long task_id, Duration duration) {
	  this.color = color;
	  this.task_id = task_id;
	  this.duration = duration;
	  this.start_angle = 0;
	  this.end_angle = 0;
	}
	
	public void set_angles(float start_angle, float end_angle) {
	  this.start_angle = start_angle;
	  this.end_angle = end_angle;
	}
	
	@Override
	public boolean equals(Object obj) {
	  return this.color == ((InformationPair)obj).color;
	}
  }
  
  private Vector<InformationPair> information_pairs;
  
  public StatisticView(Context context) {
    super(context);
    mPaint = new Paint();
    strokePaint = new Paint();
    cursor_is_all = true;
    db = new TaskDbAdapter(this.getContext());
    db.open();
    cursor = db.fetchAllTaskLists();
    cursor.moveToFirst();
    first_start_date = new DateTime();
    last_start_date = new DateTime();
    first_start_date_set = false;
    last_start_date_set = false;
    random = new Random();
    information_pairs = new Vector<InformationPair>();
    start_angle = 0;
    initInformation();
  }
  
  public StatisticView(Context context, AttributeSet attrs) {
    super(context, attrs);
    mPaint = new Paint();
    strokePaint = new Paint();
    cursor_is_all = true;
    db = new TaskDbAdapter(this.getContext());
    db.open();
    cursor = db.fetchAllTaskLists();
    cursor.moveToFirst();
    first_start_date = new DateTime();
    last_start_date = new DateTime();
    first_start_date_set = false;
    last_start_date_set = false;
    random = new Random();
    information_pairs = new Vector<InformationPair>();
    start_angle = 0;
    initInformation();
  }
  
  public StatisticView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    mPaint = new Paint();
    strokePaint = new Paint();
    cursor_is_all = true;
    db = new TaskDbAdapter(this.getContext());
    db.open();
    cursor = db.fetchAllTaskLists();
    cursor.moveToFirst();
    first_start_date = new DateTime();
    last_start_date = new DateTime();
    first_start_date_set = false;
    last_start_date_set = false;
    random = new Random();
    information_pairs = new Vector<InformationPair>();
    start_angle = 0;
    initInformation();
  }
  
  public void setCursor(long id) {
	if (id == 0) {
	  cursor = db.fetchAllTaskLists();
	  cursor.moveToFirst();
	  information_pairs = new Vector<InformationPair>();
	  cursor_is_all = true;
	  initInformation();
	  start_angle = 0;
	} else {
	  cursor = db.fetchAllTasksInList(id, false);
	  cursor.moveToFirst();
      information_pairs = new Vector<InformationPair>();
	  cursor_is_all = false;
	  initInformation();
      start_angle = 0;
	}
  }
  
  @Override
  public void onDraw(Canvas canvas) {
	super.onDraw(canvas);
    drawPie(canvas);
  }
  
  private void initInformation() {
	information_pairs = new Vector<InformationPair>();
	sum_of_durations = Duration.ZERO;
	if (cursor_is_all) {
	  for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
		long id = cursor.getLong(cursor.getColumnIndex(TaskDbAdapter.KEY_TASKLIST_TLID));
		int color = random_a_color();
		Cursor task_cursor = db.fetchAllTasksInList(id, false);
		Duration duration = Duration.ZERO;
		for (task_cursor.moveToFirst(); !task_cursor.isAfterLast(); task_cursor.moveToNext()) {
		  Duration duration_of_a_task = Duration.ZERO;
	      if (first_start_date_set && !last_start_date_set) {
		    duration_of_a_task = db.timeSpentOnTaskAfterSpecifiedDate(id, first_start_date);
	      } else if (first_start_date_set && last_start_date_set){
			duration_of_a_task = db.timeSpentOnTaskInBetween(id, first_start_date, last_start_date);
	      } else if (!first_start_date_set && last_start_date_set) {
			duration_of_a_task = db.timeSpentOnTaskBeforeSpecifiedDate(id, last_start_date);
	      } else {
	    	duration_of_a_task = db.timeSpentOnTask(id);
	      }
		  duration = duration.plus(duration_of_a_task);
	    }
		sum_of_durations = sum_of_durations.plus(duration);
		InformationPair new_pair = new InformationPair(color, id, duration);
		information_pairs.add(new_pair);
	  }
	} else {
	  for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {	
		long id = cursor.getLong(cursor.getColumnIndex(TaskDbAdapter.KEY_TASK_TID));
		int color = random_a_color();		  
		Duration duration_of_a_task = Duration.ZERO;
	    if (first_start_date_set && !last_start_date_set) {
		  duration_of_a_task = db.timeSpentOnTaskAfterSpecifiedDate(id, first_start_date);
	    } else if (first_start_date_set && last_start_date_set){
	      duration_of_a_task = db.timeSpentOnTaskInBetween(id, first_start_date, last_start_date);
	    } else if (!first_start_date_set && last_start_date_set) {
		  duration_of_a_task = db.timeSpentOnTaskBeforeSpecifiedDate(id, last_start_date);
	    } else {
	      duration_of_a_task = db.timeSpentOnTask(id);
	    }
		sum_of_durations = sum_of_durations.plus(duration_of_a_task);
		InformationPair new_pair = new InformationPair(color, id, duration_of_a_task);
		information_pairs.add(new_pair);
	  }
	} 
  }
  
  private void drawPie(Canvas canvas) {
	int window_width = this.getWidth();
	int window_height = this.getHeight();
	int radius = (window_width > window_height)? window_height * 48 / 100 : window_width * 48 / 100;
	mPaint.setAntiAlias(true);
	mPaint.setStyle(Paint.Style.FILL);
	RectF rect = new RectF(window_width / 2 - radius, window_height / 2 - radius,
						   window_width / 2 + radius, window_height / 2 + radius);
	strokePaint.setColor(Color.BLACK);
	strokePaint.setAntiAlias(true);
	strokePaint.setStyle(Paint.Style.STROKE);
	strokePaint.setStrokeWidth(WIDTHOFDARKSTROKE);
	long sum_milliseconds = sum_of_durations.getMillis();
	if (sum_milliseconds == 0) {
	  canvas.drawArc(rect, 0, 360, true, strokePaint);
	  return;
	}
	boolean warn_angle = false;
	float moving_angle = start_angle;
	for (int draw_i = 0; draw_i < information_pairs.size(); draw_i++) {
	  InformationPair current_pair = information_pairs.get(draw_i);
	  mPaint.setColor(current_pair.color);
	  float sweep_angle = 360 * current_pair.duration.getMillis() / sum_milliseconds;
	  if (sweep_angle < WARNINGANGLE) {
		warn_angle = true;
	  }
	  if (draw_i == information_pairs.size() - 1) {
		sweep_angle = start_angle - moving_angle;
		if (sweep_angle <= 0) {
		  sweep_angle += 360;
		}
	  }
	  Log.d("Stat", "start_angle is " + start_angle);
      canvas.drawArc(rect, moving_angle, sweep_angle, true, mPaint);
	  canvas.drawArc(rect, moving_angle, sweep_angle, true, strokePaint);
	  current_pair.set_angles(moving_angle, moving_angle + sweep_angle);
	  moving_angle += sweep_angle;
	  if (moving_angle >= 360) {
		moving_angle %= 360;
	  }
	}
	if (warn_angle) {
	  // To do
	}
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    //int point = event.getPointerCount();
    if (true /*point == 1*/) { //Do not know what will happen when multi-touching yet.
      // This rotation may not have the best user experience.
      float pressed_x = event.getX();
      float pressed_y = event.getY();
      int action = event.getAction();
      int window_width = this.getWidth();
      int window_height = this.getHeight();
	  int radius = (window_width > window_height)? window_height * 48 / 100 : window_width * 48 / 100;
      switch (action) {
        case MotionEvent.ACTION_DOWN:
      	  if (Math.sqrt((pressed_x - window_width / 2) * (pressed_x - window_width / 2) + 
					    (pressed_y - window_height/ 2) * (pressed_y - window_height / 2)) > radius) {
      		return false;
      	  }
      	  
          last_x = pressed_x;
          last_y = pressed_y;
          break;
        case MotionEvent.ACTION_MOVE:
    	  double OtoLast = Math.sqrt((last_x - window_width / 2) * (last_x - window_width / 2) + 
    		  			 		     (last_y - window_height/ 2) * (last_y - window_height / 2));
    	  double OtoPressed = Math.sqrt((pressed_x - window_width / 2) * (pressed_x - window_width / 2) + 
		 		   					    (pressed_y - window_height/ 2) * (pressed_y - window_height / 2));
    	  double PresstoLast = Math.sqrt((last_x - pressed_x) * (last_x - pressed_x) + 
	   		      	                     (last_y - pressed_y) * (last_y - pressed_y));
    	  if (OtoPressed == 0 || OtoLast == 0 || PresstoLast == 0) {
    	    break;
    	  }
    	  double cosPressLast = (OtoLast * OtoLast + OtoPressed * OtoPressed - PresstoLast * PresstoLast) /
    				   (2 * OtoLast * OtoPressed);
    	  double cosPressedO = (pressed_x - window_width) / OtoPressed;
    	  double cosLastO = (last_x - window_width) / OtoLast;
    	  double angle_changed = Math.acos(cosPressLast) * 180 / Math.PI;
        
    	  if (last_y == window_height / 2 && pressed_y == window_height / 2) {
    	    break;
    	  } else if (last_y <= window_height / 2 && pressed_y <= window_height / 2) {
    	    if (cosPressedO <= cosLastO) {
    		  start_angle -= angle_changed;
    	    } else {
    	   	  start_angle += angle_changed;
    	    }
    	  } else if (last_y >= window_height / 2 && pressed_y >= window_height / 2) {
      	    if (cosPressedO >= cosLastO) {
      		  start_angle -= angle_changed;
      	    } else {
      		  start_angle += angle_changed;
      	    }
    	  } else if (last_y <= window_height / 2 && pressed_y >= window_height / 2) {
    	    float cross = last_x + (pressed_x - last_x) / (pressed_y - last_y) * (0 - last_y) - window_width / 2;
      	    if (cross < 0) {
      		  start_angle -= angle_changed;
      	    } else if (cross > 0) {
      		  start_angle += angle_changed;
      	    } else {
      	      break;
      	    }
    	  } else if (last_y >= window_height / 2 && pressed_y <= window_height / 2) {
      	    float cross = last_x + (pressed_x - last_x) / (pressed_y - last_y) * (0 - last_y) - window_width / 2;
      	    if (cross < 0) {
      		  start_angle += angle_changed;
      	    } else if (cross > 0) {
      		  start_angle -= angle_changed;
      	    } else {
      		  break;
      	    }
    	  }
    	  if (start_angle >= 360) {
    		start_angle %= 360;
    	  } else if (start_angle < 0) {
    		start_angle %= 360;
    		start_angle += 360;
    	  }
    	  invalidate();
    	  last_x = pressed_x;
    	  last_y = pressed_y;
    	  break;
        case MotionEvent.ACTION_UP:
          float angle_chosen = 270 - start_angle;
          angle_chosen = (angle_chosen >= 0)? angle_chosen : angle_chosen + 360;
          for (int find_id = 0; find_id < information_pairs.size(); find_id++) {
        	InformationPair current_pair = information_pairs.get(find_id);
        	if (current_pair.start_angle <= angle_chosen && current_pair.end_angle >= angle_chosen) {
              update_status(current_pair);
        	  break;
        	}
          }
          break;
        }
      } /*else {
        Log.i("Stat", "Cannot support multiple touch in rotation yet.");
      }*/
    return true;
  }
  
  @Override 
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
	int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
	this.setMeasuredDimension(parentWidth, parentWidth);
  }
  
  private void update_status(InformationPair chosen_pair) {
	long id = chosen_pair.task_id;
	int color = chosen_pair.color;
	String name;
	if (cursor_is_all) {
	  Cursor c = db.fetchTaskList(id);
	  c.moveToFirst();
	  name = c.getString(c.getColumnIndex(TaskDbAdapter.KEY_TASKLIST_TLNAME));
	  name = "Target tasklist: " + name;
	} else {
      Cursor c = db.fetchTask(id);
      c.moveToFirst();
      name = c.getString(c.getColumnIndex(TaskDbAdapter.KEY_TASK_NAME));
      name = "Target task: " + name;
	}
	
	Duration duration = chosen_pair.duration;
	long days = duration.getStandardDays();
	long hours = duration.getStandardHours();
	long minutes = duration.getStandardMinutes();
	long seconds = duration.getStandardSeconds();
	seconds -= minutes * 60;
	minutes -= hours * 60;
	hours -= days * 24;
	
	TextView name_view = (TextView)((Activity)getContext()).findViewById(R.id.stat_task_name);
	TextView time_view = (TextView)((Activity)getContext()).findViewById(R.id.stat_task_time);
	TaskColorIndicatorView indicator = (TaskColorIndicatorView)((MainActivity)getContext())
																.getPagerAdapter().getItem(2).getView()
																.findViewById(R.id.taskcolorindicator);
	Log.d("StatisticView",""+indicator);
	//indicator.setColor(color);
	name_view.setText(name);
	time_view.setText("Time: " + days + "d " + hours + "h " +
					  minutes + "m " + seconds + "s");
  }
  
  private int random_a_color() {
	InformationPair query;
	int color;
	if (information_pairs.size() >= GoodColor.BRIGHTCOLOR.length) {
	  Log.i("Color", "Not enough color!");
	  return 0xffffff;
	}
	do {
	  int color_index = random.nextInt(GoodColor.BRIGHTCOLOR.length);
	  color = GoodColor.BRIGHTCOLOR[color_index];
	  query = new InformationPair(color, 0, null);
	} while (information_pairs.indexOf(query) != -1);
	return color;
  }
}
