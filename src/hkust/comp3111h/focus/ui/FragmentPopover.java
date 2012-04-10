package hkust.comp3111h.focus.ui;

import greendroid.widget.QuickAction;
import greendroid.widget.QuickActionWidget;

import java.util.List;

import android.content.Context;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.view.View.MeasureSpec;
import android.view.Display;
import android.view.WindowManager;

import hkust.comp3111h.focus.R;

/**
 * Subclass QuickActionWidget A QuckActionWidget is displayed on top of the user
 * interface (it overlaps all UI elements but the notification bar)
 */
public class FragmentPopover extends QuickActionWidget {
  // for graphical translation
  protected DisplayMetrics metrics;

  public FragmentPopover(Context context, int layout) {
    super(context);
    setContentView(layout);
    metrics = context.getResources().getDisplayMetrics();

    setFocusable(true);
    setTouchable(true);
  }

  public void setContent(View content) {
    // set the contianer to the root
    FrameLayout contentContainer = (FrameLayout) getContentView().findViewById(
        R.id.content);
    contentContainer.addView(content);
  }

  public void setContent(View content, LayoutParams params) {
    FrameLayout contentContainer = (FrameLayout) getContentView().findViewById(
        android.R.id.content);
    contentContainer.addView(content, params);

  }

  @Override
  protected void populateQuickActions(List<QuickAction> quickActins) {
    // Nothing
  }

  /**
   * Measure the layout, see document of the greendroid
   * 
   * @param anchorRect
   * @param contentView
   */
  @Override
  protected void onMeasureAndLayout(Rect anchorRect, View contentView) {
    contentView.setLayoutParams(new FrameLayout.LayoutParams(
        ViewGroup.LayoutParams.WRAP_CONTENT,
        ViewGroup.LayoutParams.WRAP_CONTENT));
    contentView.measure(
        MeasureSpec.makeMeasureSpec(getScreenWidth(), MeasureSpec.EXACTLY),
        ViewGroup.LayoutParams.WRAP_CONTENT);
    int rootHeight = contentView.getMeasuredHeight();
    int offsetY = getArrowOffsetY();
    int dyTop = anchorRect.top;
    int dyBottom = getScreenHeight() - anchorRect.bottom;

    boolean onTop = (dyTop > dyBottom);
    int popupY = (onTop) ? anchorRect.top - rootHeight + offsetY
        : anchorRect.bottom - offsetY;
    setWidgetSpecs(popupY, onTop);

  }
  @Override
  public void show(View anchor) {
    if(isShowing()) {
      return;
    }
    super.show(anchor);
  }
}
