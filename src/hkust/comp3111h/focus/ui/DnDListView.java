package hkust.comp3111h.focus.ui;
import hkust.comp3111h.focus.Activity.MainActivity;
import hkust.comp3111h.focus.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.util.Log;
import android.view.Display;
import android.graphics.Point;
import android.app.Activity;
/**
 * @class DnDListView
 *       The very basic drag and drop list view implementation
 *       support drag and drop to do reorder
 *       pull to add new item
 *
 */

public class DnDListView extends ListView {
  //Constants for pulling to add task
  private static final float PULL_RESISTANCE = 1.7f;
  private static final int BOUNCE_ANIMATION_DURATION = 700;
  private static final int BOUNCE_ANIMATION_DELAY = 100;
  private static final float BOUNCE_OVERSHOOT_TENSION = 1.4f;
  private static final int ROTATE_ARROW_ANIMATION_DURATION = 250;

  //A enum to record the pulling state
  private static enum State {
    PULL_TO_ADD,
    RELEASE_TO_ADD,
    ADD
  }
  private static int measuredHeaderHeight;
  //Variables for pulling
  private float previousY;
  private float headerPadding;
  private boolean hasResetHeader;
  private boolean bounceBackHeader;

  //For pulling
  private State mPullingState;
  private LinearLayout mHeaderContainer;
  private RelativeLayout mHeader;
  private RotateAnimation mFlipAnimation;
  private RotateAnimation mReverseFlipAnimation;
  private ImageView mPullHeaderImage;
  private TextView mPullHeaderText;


  boolean mDragMode;
  boolean isDraging;
  int mStartPosition;
  int mEndPosition;
  int mDragPointOffset; // Used to adjust drag view location

  ImageView mDragView; //The image to be draged, it's the generated view image basically
  GestureDetector mGestureDetector; //detect gueture, not working currently

  DropListener mDropListener;
  RemoveListener mRemoveListener;
  DragListener mDragListener;
  OnPullListener mPullListener;
  

  /**
   * Constructor, setting up context, nothing sepcial
   */
  public DnDListView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  /**
   * Initialization
   */
  private void init() {
    //Get the views
    setVerticalFadingEdgeEnabled(false);
    mHeaderContainer = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.pull_header_layout, null);
    mHeader = (RelativeLayout) mHeaderContainer.findViewById(R.id.header);
    mPullHeaderText = (TextView) mHeader.findViewById(R.id.text); 
    mPullHeaderImage = (ImageView) mHeader.findViewById(R.id.image);

    //initialize animation
    mFlipAnimation = new RotateAnimation(0,-180, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
    mFlipAnimation.setInterpolator(new LinearInterpolator());
    mFlipAnimation.setDuration(ROTATE_ARROW_ANIMATION_DURATION);
    mFlipAnimation.setFillAfter(true);

    mReverseFlipAnimation = new RotateAnimation(-180,0, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
    mReverseFlipAnimation.setInterpolator(new LinearInterpolator());
    mReverseFlipAnimation.setDuration(ROTATE_ARROW_ANIMATION_DURATION);
    mReverseFlipAnimation.setFillAfter(true);

    addHeaderView(mHeaderContainer);
    setPullingState(State.PULL_TO_ADD);

    ViewTreeObserver vto = mHeader.getViewTreeObserver();
    vto.addOnGlobalLayoutListener(new TaskListOnGlobalLayoutListener());
  }

  private void setHeaderPadding(int padding) {
    headerPadding = padding;
    MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) mHeader.getLayoutParams();
    mlp.setMargins(0,padding, 0,0);
    mHeader.setLayoutParams(mlp);
  }

  /**
   * Drop listener
   */
  public void setDropListener(DropListener l) {
    mDropListener = l;
  }

  /**
   * RemoveListener
   */
  public void setRemoveListener(RemoveListener l) {
    mRemoveListener = l;
  }

  /**
   * DragListener
   */
  public void setDragListener(DragListener l) {
    mDragListener = l;
  }

/**
 * Handle the touching event
 */
  @Override
  public boolean onTouchEvent(MotionEvent ev) {
    final int action = ev.getAction();
    final int x = (int) ev.getX();
    final int y = (int) ev.getY();
    boolean returnVal = true;

    //the drag begins when we touch the first quater of the item
    if (isDraging||action == MotionEvent.ACTION_DOWN && x < this.getWidth() / 4) {
      mDragMode = true;
    } else {
      mDragMode = false;
    }

    //propagate the event to the parent if not drag event
    if (mDragMode) {

    //Start handling drag and drop here, basic procedure
    //1. get the current position when touched
    //2. Move it as user drag
    //3. handle the drop, first check whether it's done position, yes, mark done
    //   If not, check whether it is invalid position, if yes, nothing happen
    //   If the position is valid, do swapping
      switch (action) {
        //Detect the beginning of the drag
        case MotionEvent.ACTION_DOWN:
          isDraging = true;
          mStartPosition = pointToPosition(x, y);
          Log.d("Drag and Drop","Start at: "+ mStartPosition);
          if (mStartPosition != INVALID_POSITION) {
            int mItemPosition = mStartPosition - getFirstVisiblePosition();
            mDragPointOffset = y - getChildAt(mItemPosition).getTop();
            mDragPointOffset -= ((int) ev.getRawY()) - y;
            startDrag(mItemPosition, y);
            drag(0, y);// replace 0 with x if desired
          }
          break;
          //Drag
        case MotionEvent.ACTION_MOVE:
          drag(0, y);// replace 0 with x if desired
          int hoverPosition = pointToPosition(x,y);
          if(hoverPosition >= 0 ) {
            doExpansion(hoverPosition);
          }
          break;
        case MotionEvent.ACTION_CANCEL:
        case MotionEvent.ACTION_UP:
          //Drop
        default:
          isDraging =false;
          mDragMode = false;
          mEndPosition = pointToPosition(x, y);
          Log.d("DnD","End at:" +mEndPosition);
          stopDrag(mStartPosition - getFirstVisiblePosition());
          if (y > getHeight() - 15) {
            mRemoveListener.onRemove(mStartPosition);
          } else if (mDropListener != null && mStartPosition != INVALID_POSITION
              && mEndPosition != INVALID_POSITION) {
            mDropListener.onDrop(mStartPosition, mEndPosition);
          }
          resetViews();

          break;
      }
      return true;
    } 

    switch(action) {
      case MotionEvent.ACTION_DOWN:
        if(getFirstVisiblePosition() == 0)
          previousY = y;
        else
          previousY = -1;
        break;
      case MotionEvent.ACTION_UP:
        if(previousY !=-1 && (mPullingState == State.RELEASE_TO_ADD||getFirstVisiblePosition() == 0)) {
          switch(mPullingState) {
            case RELEASE_TO_ADD:
              setPullingState(State.ADD);
              bounceBackHeader();
              ((MainActivity)getContext()).showQuickAddDialog();
              break;
            case PULL_TO_ADD:
              resetHeader();
              break;
          }
        }
        break;
      case MotionEvent.ACTION_MOVE:
        if(previousY !=-1) {
          float diff = y - previousY;
          if(diff > 0) diff/=PULL_RESISTANCE;
          previousY=y;

          int newHeaderPadding = (int)Math.max(headerPadding + Math.round(diff), -mHeader.getHeight());
          if(mPullingState == State.ADD && newHeaderPadding > 0) {
            newHeaderPadding =0;
          }

          setHeaderPadding(newHeaderPadding);
          if(mPullingState == State.PULL_TO_ADD && headerPadding > 0) {
            setPullingState(State.RELEASE_TO_ADD);
            mPullHeaderImage.clearAnimation();
            mPullHeaderImage.startAnimation(mFlipAnimation);
          }else if(mPullingState == State.RELEASE_TO_ADD && headerPadding <0) {
            setPullingState(State.PULL_TO_ADD);
            mPullHeaderImage.clearAnimation();
            mPullHeaderImage.startAnimation(mReverseFlipAnimation);
          }
        }
        break;
    }
    return super.onTouchEvent(ev);
  }

  private void bounceBackHeader() {
    Log.d("Animation","bounceBack");
    int yTranslate = -mHeaderContainer.getHeight();
      TranslateAnimation bounceAnimation = new TranslateAnimation(
        TranslateAnimation.ABSOLUTE, 0,
        TranslateAnimation.ABSOLUTE, 0,
        TranslateAnimation.ABSOLUTE, 0,
        TranslateAnimation.ABSOLUTE, yTranslate);
    bounceAnimation.setDuration(BOUNCE_ANIMATION_DURATION);
    bounceAnimation.setFillEnabled(true);
    bounceAnimation.setFillAfter(false);
    bounceAnimation.setFillBefore(true);
    bounceAnimation.setInterpolator(new OvershootInterpolator(BOUNCE_OVERSHOOT_TENSION));
    bounceAnimation.setAnimationListener(new HeaderAnimationListener());
    startAnimation(bounceAnimation);
  }


  private void resetHeader() {
    if(headerPadding == -mHeader.getHeight() || getFirstVisiblePosition() >0) {
      setPullingState(State.PULL_TO_ADD);
      return;
    }
    if(getAnimation()!= null && !getAnimation().hasEnded()) {
      bounceBackHeader = true;
    } else {
      bounceBackHeader();
    }
  }

  //set pulling state
  private void setPullingState(State state) {
    this.mPullingState = state;
    switch(state) {
      case PULL_TO_ADD:
        mPullHeaderImage.setVisibility(View.VISIBLE);
        mPullHeaderText.setText(R.string.pull_to_add);
        break;
      case RELEASE_TO_ADD:
        mPullHeaderImage.setVisibility(View.VISIBLE);
        mPullHeaderText.setText(R.string.release_to_add);
        break;
      case ADD:
        mPullHeaderImage.clearAnimation();
        mPullHeaderImage.setVisibility(View.INVISIBLE);
        setPullingState(State.PULL_TO_ADD);
        break;
    }
  }

  @Override
  protected void onScrollChanged(int l, int t, int oldl, int oldt) {
    super.onScrollChanged(l, t, oldl, oldt);
    if(!hasResetHeader) {
      if(measuredHeaderHeight > 0 && mPullingState != State.ADD) {
        setHeaderPadding(-measuredHeaderHeight);
      }
      hasResetHeader = true;
    }
  }

  private class HeaderAnimationListener implements AnimationListener {
    private int height;
    private State stateAtAnimationStart;
    @Override
    public void onAnimationStart(Animation animation) {
      stateAtAnimationStart = mPullingState;
      android.view.ViewGroup.LayoutParams lp = getLayoutParams();
      height = lp.height;
      lp.height = getHeight() + mHeaderContainer.getHeight();
      setLayoutParams(lp);
    }

    @Override
    public void onAnimationEnd(Animation animation) {
      setHeaderPadding(-mHeader.getHeight());
      android.view.ViewGroup.LayoutParams lp = getLayoutParams();
      lp.height = height;
      setLayoutParams(lp);
      if(bounceBackHeader) {
        bounceBackHeader = false;
        postDelayed(new Runnable() {
          @Override 
          public void run() {
            bounceBackHeader();
          }
        }, BOUNCE_ANIMATION_DELAY);
      }else if(stateAtAnimationStart != State.ADD) {
        setPullingState(State.PULL_TO_ADD);
      }
    }
    @Override 
    public void onAnimationRepeat(Animation animation){}
  }

  
  /**
   * Do the drag, basically do two things:
   * 1. Calculate the new position based on the touched position 
   * 2. Calculate the relative position to the parent
   * 3. Set the position
   * 4. Call the handler(s)
   */
  private void drag(int x, int y) {
    if (mDragView != null) {
      WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) mDragView
          .getLayoutParams();
      layoutParams.x = x;
      layoutParams.y = y - mDragPointOffset;
      WindowManager mWindowManager = (WindowManager) getContext()
          .getSystemService(Context.WINDOW_SERVICE);
      mWindowManager.updateViewLayout(mDragView, layoutParams);
      if (mDragListener != null)
        mDragListener.onDrag(x, y, null);// change null to "this" when ready to
    }
  }

  // enable the drag view for dragging
  // Initialize parameters basically
  private void startDrag(int itemIndex, int y) {
    stopDrag(itemIndex);

    View item = getChildAt(itemIndex);
    if (item == null)
      return;
    item.setDrawingCacheEnabled(true);
    if (mDragListener != null)
      mDragListener.onStartDrag(item);

    // Create a copy of the drawing cache so that it does not get recycled
    // by the framework when the list tries to clean up memory
    Bitmap bitmap = Bitmap.createBitmap(item.getDrawingCache());
    WindowManager.LayoutParams mWindowParams = new WindowManager.LayoutParams();
    mWindowParams.gravity = Gravity.TOP;
    mWindowParams.x = 0;
    mWindowParams.y = y - mDragPointOffset;

    mWindowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
    mWindowParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
    mWindowParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
        | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
    mWindowParams.format = PixelFormat.TRANSLUCENT;
    mWindowParams.windowAnimations = 0;

    Context context = getContext();
    ImageView v = new ImageView(context);
    v.setImageBitmap(bitmap);

    WindowManager mWindowManager = (WindowManager) context
        .getSystemService(Context.WINDOW_SERVICE);
    mWindowManager.addView(v, mWindowParams);
    mDragView = v;
  }

  // destroy drag view
  private void stopDrag(int itemIndex) {
    if (mDragView != null) {
      if (mDragListener != null)
        mDragListener.onStopDrag(getChildAt(itemIndex));
      mDragView.setVisibility(GONE);
      WindowManager wm = (WindowManager) getContext().getSystemService(
          Context.WINDOW_SERVICE);
      wm.removeView(mDragView);
      mDragView.setImageDrawable(null);
      mDragView = null;
    }
  }
  private void doExpansion(int draggingItemHoverPosition) {
    int expandItemViewIndex = draggingItemHoverPosition - getFirstVisiblePosition();
    /*
    if(draggingItemHoverPosition >= mStartPosition) {
      expandItemViewIndex++;
    }
    */
    View draggingItemOriginalView = getChildAt(
        mStartPosition -getFirstVisiblePosition());
    int originHeight = draggingItemOriginalView.getHeight();
    for(int i=0;; i++) {
      View itemView = getChildAt(i);
      if(itemView == null) {
        break;
      }
      ViewGroup.LayoutParams params = itemView.getLayoutParams();
      int height = LayoutParams.WRAP_CONTENT;
      if(itemView.equals(draggingItemOriginalView)) {
        height = 1;
      } else if(i== expandItemViewIndex) {
        height = 100;
      }
      params.height = height;
      itemView.setLayoutParams(params);
    }

  }
  private void resetViews() {
    for(int i=0; ;i++) {
      View v = getChildAt(i);
      if(v==null) {
        layoutChildren();
        v = getChildAt(i);
        if(v==null) {
          break;
        }
      }
      ViewGroup.LayoutParams params = v.getLayoutParams();
      params.height = LayoutParams.WRAP_CONTENT;
      v.setLayoutParams(params);
    }
  }

  // Slide to the two side to remove the item
  // Not working currently
  private GestureDetector createFlingDetector() {
    return new GestureDetector(getContext(), new SimpleOnGestureListener() {
      @Override
      public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
          float velocityY) {
        if (mDragView != null) {
          int deltaX = (int) Math.abs(e1.getX() - e2.getX());
          int deltaY = (int) Math.abs(e1.getY() - e2.getY());

          if (deltaX > mDragView.getWidth() / 2
              && deltaY < mDragView.getHeight()) {
            mRemoveListener.onRemove(mStartPosition);
          }
          stopDrag(mStartPosition - getFirstVisiblePosition());
          return true;
        }
        return false;
      }
    });
  }
  private class TaskListOnGlobalLayoutListener implements OnGlobalLayoutListener {
    @Override 
    public void onGlobalLayout() {
      int initialHeaderHeight = mHeader.getHeight();
      if(initialHeaderHeight > 0) {
        measuredHeaderHeight = initialHeaderHeight;
        if(measuredHeaderHeight > 0 && mPullingState != State.ADD) {
          setHeaderPadding(-measuredHeaderHeight);
          requestLayout();
        }
      }
      getViewTreeObserver().removeGlobalOnLayoutListener(this);
    }
  }
}
