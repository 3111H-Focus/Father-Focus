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
	
  public TaskColorIndicatorView(Context context, AttributeSet attrs) {
    super(context);
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
	
  @Override
  public void onDraw(Canvas canvas) {
	super.onDraw(canvas);
	int window_width = this.getWidth();
	int window_height = this.getHeight();
	mPaint.setColor(color);
	canvas.drawRoundRect(new RectF(0, 0, window_width, window_height), window_width / 10, window_height / 10, mPaint);
	canvas.drawRoundRect(new RectF(0, 0, window_width, window_height), 
			             window_width / 10, window_height / 10, strokePaint);
  } 
	
  @Override 
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
	int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
	this.setMeasuredDimension(parentWidth, parentWidth);
  }
}
