package hkust.comp3111h.focus.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.MeasureSpec;



public class TaskColorIndicatorView extends View {
  private final int WIDTHOFDARKSTROKE = 5;
	  
  private int color;
  private Paint mPaint;
  private Paint strokePaint;
	  
  public TaskColorIndicatorView(Context context) {
	super(context);
    color = 0xffffffff;
	mPaint = new Paint();
	mPaint.setColor(color);
	mPaint.setAntiAlias(true);
	mPaint.setStyle(Paint.Style.FILL);
	strokePaint = new Paint();
	strokePaint.setColor(Color.BLACK);
	strokePaint.setAntiAlias(true);
	strokePaint.setStyle(Paint.Style.STROKE);
	strokePaint.setStrokeWidth(WIDTHOFDARKSTROKE);
  }
	
  public TaskColorIndicatorView(Context context, AttributeSet attrs) {
    super(context, attrs);
	color = 0xffffffff;
    mPaint = new Paint();
    mPaint.setColor(color);
	mPaint.setAntiAlias(true);
	mPaint.setStyle(Paint.Style.FILL);
	strokePaint = new Paint();
	strokePaint.setColor(Color.BLACK);
	strokePaint.setAntiAlias(true);
	strokePaint.setStyle(Paint.Style.STROKE);
	strokePaint.setStrokeWidth(WIDTHOFDARKSTROKE);
  }
	
  public TaskColorIndicatorView(Context context, AttributeSet attrs,
								int defStyle) {
	super(context, attrs, defStyle);
	color = 0xff000000;
	mPaint = new Paint();
	mPaint.setColor(color);
	mPaint.setAntiAlias(true);
	mPaint.setStyle(Paint.Style.FILL);
	strokePaint = new Paint();
	strokePaint.setColor(Color.BLACK);
	strokePaint.setAntiAlias(true);
	strokePaint.setStyle(Paint.Style.STROKE);
	strokePaint.setStrokeWidth(WIDTHOFDARKSTROKE);
  }
	
  public void setColor(int color) {
	this.color = color;
	invalidate();
  }
  
  @Override
  public void onDraw(Canvas canvas) {
	super.onDraw(canvas);
	float window_width = this.getWidth();
	float window_height = this.getHeight();
	float radius = (window_width > window_height)? window_height * 40 / 100 : window_width * 40 / 100;
	mPaint.setColor(color);
	canvas.drawCircle(window_width / 2, window_height / 2, radius, mPaint);
	canvas.drawCircle(window_width / 2, window_height / 2, radius, strokePaint);
  } 
	
  @Override 
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
	int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
	this.setMeasuredDimension(parentWidth, parentWidth);
  }
}
