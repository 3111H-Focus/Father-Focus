package hkust.comp3111h.focus.ui;
/**
 * Interface to implemment when you want to get notified of 'pull to add'
 * Call OnPullReleaseListener(...) to activate 
 */
public interface OnPullListener{
  /**
   * Method to be called when a pull is requested
   */
  public void onPull();

}
