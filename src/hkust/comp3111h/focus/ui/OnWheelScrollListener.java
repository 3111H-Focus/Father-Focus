package hkust.comp3111h.focus.ui;
/**
 * Wheel scrolled listener infterface
 */

public interface OnWheelScrollListener {
  /**
   * Callback method to be invoked when scrolling started.
   * @param wheel the wheel view whose state has changed.
   */
  void onScrollingStarted(WheelView wheel);
  /**
   * Callback method to be invoked when scrolling started
   * @param wheel teh whell view whose state has changed
   */
  void onScrollingFinished(WheelView wheel);
}
