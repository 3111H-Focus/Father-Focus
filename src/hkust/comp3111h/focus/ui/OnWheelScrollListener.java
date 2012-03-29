package hkust.comp3111h.focus.ui;

/**
 * @class
 *        Wheel scrolled listener infterface. Definition for a callback to be 
 *        invoked when teh list or gird has been scrolled. 
 */

public interface OnWheelScrollListener {
  /**
   * Callback method to be invoked when scrolling started.
   * 
   * @param wheel
   *          the wheel view whose state has changed.
   */
  void onScrollingStarted(WheelView wheel);

  /**
   * Callback method to be invoked when scrolling finished
   * 
   * @param wheel
   *          the whell view whose state has changed
   */
  void onScrollingFinished(WheelView wheel);
}
