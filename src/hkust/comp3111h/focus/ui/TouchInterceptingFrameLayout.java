package hkust.comp3111h.focus.ui;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.FrameLayout;

public class TouchInterceptingFrameLayout extends FrameLayout {
  public interface InterceptTouchListener {
    public boolean doInterceptTouch(KeyEvent event);
  }

  private InterceptTouchListener mListener;

  public TouchInterceptingFrameLayout(Context context, AttributeSet attrs) {
    super(context, attrs);
    setBackgroundColor(Color.TRANSPARENT);
  }
  @Override
  public boolean dispatchKeyEvent(KeyEvent event) {
    if(mListener != null && mListener.doInterceptTouch(event)) {
      return true;
    }
    return super.dispatchKeyEvent(event);
  }
  public InterceptTouchListener getInterceptTouchListener() {
    return mListener;
  }
  public void setInterceptTouchListener(InterceptTouchListener mListener) {
    this.mListener = mListener;
  }
}
