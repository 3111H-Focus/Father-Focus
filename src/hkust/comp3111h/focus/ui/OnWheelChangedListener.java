package hkust.comp3111h.focus.ui;

/**
 * Wheel changed listener interface.
 * The onChanged method is called whenever current wheel positions is changed:
 * New wheel position is set
 * Wheel view is scrolled
 */

public interface OnWheelChangedListener {
  /**
   * Callback method to invoked when current item changed
   * @param wheel teh wheel view whose state has changed
   * @param oldValue the old value of current item
   * @param newValue the new value of current item
   */
  void onChanged(WheelView wheel, int oldValue, int newValue);
}
