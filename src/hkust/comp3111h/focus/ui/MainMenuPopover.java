/**
 * The implmentation of the popup menu
 */

package hkust.comp3111h.focus.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import hkust.comp3111h.focus.R;
import hkust.comp3111h.focus.ui.TouchInterceptingFrameLayout.InterceptTouchListener;

public class MainMenuPopover extends FragmentPopover implements InterceptTouchListener {
  public static final int MAIN_MENU_SETTING = R.string.main_menu_setting;
  public static final int MAIN_MENU_ABOUT = R.string.main_menu_about;
  public static final int MAIN_MENU_SORT = R.string.main_menu_sort;

  public interface MainMenuListener {
    public void mainMenuItemSelected(int item, Intent customIntent);
  }

  //Data memeber declaraction
  private MainMenuListener mListener;
  private final LayoutInflater inflater;
  private final LinearLayout content;
  private final int rowLayout;
  private boolean suppressNextKeyEvent = false;

  private final LinearLayout topFixed;
  private final LinearLayout bottomFixed;

  /**
   * @param listener
   */
  public void setMenuListener(MainMenuListener listener) {
    mListener = listener;
  }

  /**
   * Constructor
   */
  public MainMenuPopover(Context context, int layout) {
    super(context, layout);
    //The root layout should deal with the touch event interception
    TouchInterceptingFrameLayout rootLayout = (TouchInterceptingFrameLayout) getContentView();
    rootLayout.setInterceptTouchListener(this);
    rootLayout.setOnTouchListener(new OnTouchListener() {
      @Override
      public boolean onTouch(View v, MotionEvent event) {
        dismiss();
        return false;
      }
    });
    rowLayout = R.layout.main_menu_row_layout;
    inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    content = (LinearLayout) getContentView().findViewById(android.R.id.content);
    topFixed = (LinearLayout) getContentView().findViewById(R.id.topFixedItems);
    bottomFixed = (LinearLayout) getContentView().findViewById(R.id.bottomFixedItems);
    addFixedItems();
  }

  /**
   * Dealing with interceptTouch, show the popover instead of the regualr
   * android menu
   * @param event
   */
  public boolean doInterceptTouch(KeyEvent event) {
    int keyCode = event.getKeyCode();
    if(!suppressNextKeyEvent) {
      if((keyCode == KeyEvent.KEYCODE_MENU ||keyCode == KeyEvent.KEYCODE_BACK) && isShowing()) {
        dismiss();
        return true;
      }
    }
    suppressNextKeyEvent = false;
    return false;
  }
  public void supressNextKeyEvent() {
    suppressNextKeyEvent = true;
  }

  @Override
  public void setBackgroundDrawable(Drawable background) {
    super.setBackgroundDrawable(null);
  }

  /*
  @Override
  protected int getArrowLeftMargin(View arrow) {
    return mRect.centerX() - arrow.getMeasuredWidth() / 2 - (int) (12* matrics.density);
  }
  */


  //-----------------Public interfaces----
  public void addMenuItem(int title, int imageRes, int id) {
    addMenuItem(title, imageRes,id, null, content);
  }

  public void addMenuItem(int title, int imageRes, Intent customIntent, int id) {
    addMenuItem(title, imageRes, id, customIntent, content);
  }

  public void addMenuItem(
      CharSequence title, 
      Drawable image, 
      Intent customIntent,
      int id) {
    addMenuItem(title, image, id, customIntent, content);
  }

  public void addSepeartor() {
    inflater.inflate(R.layout.menu_separator, content);
  }

  public void clear() {
    content.removeAllViews();
  }

  //--- private helper
  private void addMenuItem(
      int title, int imageRes, int id, Intent customIntent, ViewGroup container) {
    View item = setupItemWithParams(title, imageRes);
    addViewWithListener(item, container, id, customIntent);
  }

  private void addMenuItem(
      CharSequence title, 
      Drawable image, 
      int id,
      Intent customIntent,
      ViewGroup container) {
    View item = setupItemWithParams(title, image);
    addViewWithListener(item, container, id, customIntent);
  }

  private void addViewWithListener(View view, ViewGroup container, 
      final int id, final Intent customIntent) {
    container.addView(view);
    view.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        dismiss();
        if(mListener != null) {
          mListener.mainMenuItemSelected(id, customIntent);
        }
      }
    });
  }
  /**
   * @param title
   * @param imageRes
   * @return View
   * setup and return the view for a single item
   */
  private View setupItemWithParams(int title, int imageRes) {
    View itemRow = inflater.inflate(rowLayout, null);

    ImageView image = (ImageView) itemRow.findViewById(R.id.icon);
    image.setImageResource(imageRes);

    TextView text = (TextView) itemRow.findViewById(R.id.name);
    text.setText(title);

    return itemRow;
  }

  private View setupItemWithParams(CharSequence title, Drawable imageDrawable) {
    View itemRow = inflater.inflate(rowLayout, null);

    ImageView image =(ImageView) itemRow.findViewById(R.id.icon);
    image.setImageDrawable(imageDrawable);

    TextView name = (TextView) itemRow.findViewById(R.id.name);
    name.setText(title);

    return itemRow;
  }
  private void addFixedItems() {
    addMenuItem(MAIN_MENU_SORT, R.drawable.setting_icon,MAIN_MENU_SORT, null, topFixed);
    addMenuItem(MAIN_MENU_ABOUT, R.drawable.setting_icon, MAIN_MENU_ABOUT, null, bottomFixed);
  }

}
