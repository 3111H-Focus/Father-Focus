package hkust.comp3111h.focus.ui;

/**
 * Wheel clikcekd interface
 * The onItemClicked method is called whenever a wheel item is clicked 
 * New Wheel position is et
 * Wheel view is scrolled
 */

public interface OnWheelClickedListener {
  /**
   * Callback method to be invoked when current item clicked
   * @param wheel the wheel view
   * @param itemIndex the index of clicked item
   */
  public void onItemClicked(WheelView wheel, int itemIndex);
}
