package hkust.comp3111h.focus.ui;

import java.util.LinkedList;
import java.util.List;

import hkust.comp3111h.focus.R;
import hkust.comp3111h.focus.Adapter.*;
import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Interpolator;
import android.widget.LinearLayout;

/**
 * @class WheelView The custom widget subclassing View, with completely rerendered
 *        drawing, using grapics library. Dedicately handle the scrolling
 *        guesture
 */
public class WheelView extends View {
  /** Consant setting up */
  /** Top and button shadows colors */
  private static final int[] SHADOWS_COLORS = new int[] { 0xFF111111,
      0x00AAAAAA, 0x00AAAAAA };

  /** Top and buttom items offset (to hide that) */
  private static final int ITEM_OFFSET_PERCENT = 10;

  /** left and right padding value */
  private static final int PADDING = 10;

  /** Default count of visible items */
  private static final int DEF_VISIBLE_ITEMS = 5;

  /** The current focusing item */
  private int currentItem = 0;

  /** Default number of visible items, user can redefine this number */
  private int visibleItems = DEF_VISIBLE_ITEMS;

  /** The height of each item */
  private int itemHeight = 0;

  /**
   * Drawables for the interface rerendering, to implment the 3D like interface
   */
  private Drawable centerDrawable;
  private GradientDrawable topShadow;
  private GradientDrawable bottomShadow;

  /**
   * The variables handling the scroling
   */
  private WheelScroller scroller;
  private boolean isScrollingPerformed;
  private int scrollingOffset;

  /**
   * Determine whether the wheel should be cyclic
   */
  boolean isCyclic = true;

  /**
   * Layout of each item, usr defined
   */
  private LinearLayout itemsLayout;

  /** The number of the first item */
  private int firstItem;

  /** 
   * Adapter, providing view for each item, an adaper acts as
   * a bridge between an AdapterView and the underlying data 
   * for the view
   */
  private WheelViewAdapter viewAdapter;

  /**
   * To recycle the view, provide to the adapter as the covertView, 
   * to avoid redundant inflaction, which is very expensive.
   * Mainly for performance purpose.
   */
  private WheelRecycle recycle = new WheelRecycle(this);

  // Listeners
  private List<OnWheelChangedListener> changingListeners = new LinkedList<OnWheelChangedListener>();
  private List<OnWheelScrollListener> scrollingListeners = new LinkedList<OnWheelScrollListener>();
  private List<OnWheelClickedListener> clickingListeners = new LinkedList<OnWheelClickedListener>();

  /**
   * Constructors, set context basically
   */
  public WheelView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    initData(context);
  }

  public WheelView(Context context, AttributeSet attrs) {
    super(context, attrs);
    initData(context);
  }

  public WheelView(Context context) {
    super(context);
    initData(context);
  }

  /**
   * Initiallize data, only create new scroller currently
   * @param context
   *          the context
   */
  private void initData(Context context) {
    scroller = new WheelScroller(getContext(), scrollingListener);
  }

  /**
   * Define the callback to be invoked when the list has been scrolled
   * Please look at the WheelScroller for detail information
   */
  WheelScroller.ScrollingListener scrollingListener = new WheelScroller.ScrollingListener() {
    @Override
    public void onStarted() {
      isScrollingPerformed = true;
      notifyScrollingListenersAboutStart();
    }
    
    @Override
    public void onScroll(int distance) {
      doScroll(distance);

      int height = getHeight();
      if (scrollingOffset > height) {
        scrollingOffset = height;
        scroller.stopScrolling();
      } else if (scrollingOffset < -height) {
        scrollingOffset = -height;
        scroller.stopScrolling();
      }
    }

    @Override
    public void onFinished() {
      if (isScrollingPerformed) {
        notifyScrollingListenersAboutEnd();
        isScrollingPerformed = false;
      }

      scrollingOffset = 0;
      invalidate();
    }

    @Override
    public void onJustify() {
      if (Math.abs(scrollingOffset) > WheelScroller.MIN_DELTA_FOR_SCROLLING) {
        scroller.scroll(scrollingOffset, 0);
      }
    }
  };

  /**
   * Set the the specified scrolling interpolator
   * An interpolator defines the rate of change of an animation
   *
   * This allows the basic animation effiects (alpha translate, rtate)
   * to be accelerated decelerated, repeated, etc 
   * @param interpolator
   *          the interpolator
   */
  public void setInterpolator(Interpolator interpolator) {
    scroller.setInterpolator(interpolator);
  }

  /**
   * Gets count of visible items of the wheelView
   * 
   * @return the count of visible items
   */
  public int getVisibleItems() {
    return visibleItems;
  }

  /**
   * Sets the desired count of visible items. Actual amount of visible items
   * depends on wheel layout parameters. To apply changes and rebuild view call
   * measure().
   * 
   * @param count
   *          the desired count for visible items
   */
  public void setVisibleItems(int count) {
    visibleItems = count;
  }

  /**
   * Gets view adapter
   * 
   * @return WheelViewAdapter
   */
  public WheelViewAdapter getViewAdapter() {
    return viewAdapter;
  }

  /**
   * Receives call backs when a data set has been changed, or made invalid.
   */
  private DataSetObserver dataObserver = new DataSetObserver() {
    /**
     * This method is called when the entire data set has changed
     * most like through a call to requery on a Cursor
     */
    @Override
    public void onChanged() {
      invalidateWheel(false);
    }
    /**
     * This method is called when the entire data becomes invalid
     * most likely through a call to deactiviate() or close() on a 
     * Cursor
     */
    @Override
    public void onInvalidated() {
      invalidateWheel(true);
    }
  };

  /**
   * Sets view adapter. Usually new adapters contain different views, so it
   * needs to rebuild view by calling measure().
   * 
   * @param viewAdapter
   *          the view adapter
   */
  public void setViewAdapter(WheelViewAdapter viewAdapter) {
    if (this.viewAdapter != null) {
      this.viewAdapter.unregisterDataSetObserver(dataObserver);
    }
    this.viewAdapter = viewAdapter;
    if (this.viewAdapter != null) {
      this.viewAdapter.registerDataSetObserver(dataObserver);
    }

    invalidateWheel(true);
  }

  /**
   * Adds wheel changing listener, callback to invoke when the current 
   * Item is changed
   * 
   * @param listener
   *          the listener
   */
  public void addChangingListener(OnWheelChangedListener listener) {
    changingListeners.add(listener);
  }

  /**
   * Removes wheel changing listener
   * 
   * @param listener
   *          the listener
   */
  public void removeChangingListener(OnWheelChangedListener listener) {
    changingListeners.remove(listener);
  }

  /**
   * Notifies changing listeners, it's basically a loop to go through each
   * changingListeners (user can resiger more than one listeners
   * 
   * @param oldValue
   *          the old wheel value
   * @param newValue
   *          the new wheel value
   */
  protected void notifyChangingListeners(int oldValue, int newValue) {
    for (OnWheelChangedListener listener : changingListeners) {
      listener.onChanged(this, oldValue, newValue);
    }
  }

  /**
   * Adds wheel scrolling listener, basically push the listener to a list
   * 
   * @param listener
   *          the listener
   */
  public void addScrollingListener(OnWheelScrollListener listener) {
    scrollingListeners.add(listener);
  }

  /**
   * Removes wheel scrolling listener, remove the given listener from the
   * listener lister
   * 
   * @param listener
   *          the listener
   */
  public void removeScrollingListener(OnWheelScrollListener listener) {
    scrollingListeners.remove(listener);
  }

  /**
   * Notifies listeners about starting scrolling
   */
  protected void notifyScrollingListenersAboutStart() {
    for (OnWheelScrollListener listener : scrollingListeners) {
      listener.onScrollingStarted(this);
    }
  }

  /**
   * Notifies listeners about ending scrolling
   */
  protected void notifyScrollingListenersAboutEnd() {
    for (OnWheelScrollListener listener : scrollingListeners) {
      listener.onScrollingFinished(this);
    }
  }

  /**
   * Adds wheel clicking listener
   * 
   * @param listener
   *          the listener
   */
  public void addClickingListener(OnWheelClickedListener listener) {
    clickingListeners.add(listener);
  }

  /**
   * Removes wheel clicking listener
   * 
   * @param listener
   *          the listener
   */
  public void removeClickingListener(OnWheelClickedListener listener) {
    clickingListeners.remove(listener);
  }

  /**
   * Notifies listeners about clicking
   */
  protected void notifyClickListenersAboutClick(int item) {
    for (OnWheelClickedListener listener : clickingListeners) {
      listener.onItemClicked(this, item);
    }
  }

  /**
   * Gets current value
   * 
   * @return the current value
   */
  public int getCurrentItem() {
    return currentItem;
  }

  /**
   * Sets the current item. Does nothing when index is wrong.
   * 
   * @param index
   *          the item index
   * @param animated
   *          the animation flag
   */
  public void setCurrentItem(int index, boolean animated) {
    if (viewAdapter == null || viewAdapter.getItemsCount() == 0) {
      return; // Does nothing basically
    }

    int itemCount = viewAdapter.getItemsCount();
    //Normalize it if in cyclic mode, and do nothing if not cyclic and the index
    //is invalid
    if (index < 0 || index >= itemCount) {
      if (isCyclic) {
        while (index < 0) {
          index += itemCount;
        }
        index %= itemCount;
      } else {
        return;
      }
    }
    //If the currentItem is not the given index, do setting
    if (index != currentItem) {
      if (animated) {
        int itemsToScroll = index - currentItem;
        //When it is cyclic, the number of item to scroll needed some
        //calcualtion, depending on whether the item is before the current item
        //or after.
        if (isCyclic) {
          int scroll = itemCount + Math.min(index, currentItem)
              - Math.max(index, currentItem);
          //Determine whether before or after the current item
          if (scroll < Math.abs(itemsToScroll)) {
            itemsToScroll = itemsToScroll < 0 ? scroll : -scroll;
          }
        }
        //The scroll funciton will call notifyChangingListeners and invalidate, no need here
        scroll(itemsToScroll, 0);
      } else {
        //Set the item directly if no animation is needed
        scrollingOffset = 0;
        int old = currentItem;
        currentItem = index;
        notifyChangingListeners(old, currentItem);
        invalidate();
      }
    }
  }

  /**
   * Sets the current item without animation. Does nothing when index is wrong.
   * Call the general function, nothing special
   * 
   * @param index
   *          the item index
   */
  public void setCurrentItem(int index) {
    setCurrentItem(index, false);
  }

  /**
   * Tests if wheel is cyclic. That means before the 1st item there is shown the
   * last one
   * 
   * @return true if wheel is cyclic
   */
  public boolean isCyclic() {
    return isCyclic;
  }

  /**
   * Set wheel cyclic flag
   * 
   * @param isCyclic
   *          the flag to set
   */
  public void setCyclic(boolean isCyclic) {
    this.isCyclic = isCyclic;
    invalidateWheel(false);
  }

  /**
   * Invalidates wheel
   * 
   * @param clearCaches
   *          if true then cached views will be clear
   */
  public void invalidateWheel(boolean clearCaches) {
    if (clearCaches) {
      recycle.clearAll();
      if (itemsLayout != null) {
        itemsLayout.removeAllViews();
      }
      scrollingOffset = 0;
    } else if (itemsLayout != null) {
      // cache all items
      recycle.recycleItems(itemsLayout, firstItem, new ItemsRange());
    }
    invalidate();
  }

  /**
   * Initializes resources, set up the wheel view user interface
   * centerDrawable: the drawable for the center bar indicating focusing item
   * topShadow and bottomShadow: the shadow at the top and bottom to make it 
   * more 3D like
   */
  private void initResourcesIfNecessary() {
    if (centerDrawable == null) {
      centerDrawable = getContext().getResources().getDrawable(
          R.drawable.wheel_val);
    }

    if (topShadow == null) {
      topShadow = new GradientDrawable(Orientation.TOP_BOTTOM, SHADOWS_COLORS);
    }

    if (bottomShadow == null) {
      bottomShadow = new GradientDrawable(Orientation.BOTTOM_TOP,
          SHADOWS_COLORS);
    }

    setBackgroundResource(R.drawable.wheel_bg);
  }

  /**
   * Calculates desired height for layout
   * 
   * @param layout
   *          the source layout
   * @return the desired layout height
   */
  private int getDesiredHeight(LinearLayout layout) {
    if (layout != null && layout.getChildAt(0) != null) {
      itemHeight = layout.getChildAt(0).getMeasuredHeight();
    }

    int desired = itemHeight * visibleItems - itemHeight * ITEM_OFFSET_PERCENT
        / 50;

    return Math.max(desired, getSuggestedMinimumHeight());
  }

  /**
   * Returns height of wheel item
   * 
   * @return the item height
   */
  private int getItemHeight() {
    if (itemHeight != 0) {
      return itemHeight;
    }

    if (itemsLayout != null && itemsLayout.getChildAt(0) != null) {
      itemHeight = itemsLayout.getChildAt(0).getHeight();
      return itemHeight;
    }

    return getHeight() / visibleItems;
  }

  /**
   * Calculates control width and creates text layouts
   * Tedious geometry calcualtion, nothing special
   * Draw some triangles if you really want to understand
   * 
   * @param widthSize
   *          the input layout width
   * @param mode
   *          the layout mode
   * @return the calculated control width
   */
  private int calculateLayoutWidth(int widthSize, int mode) {
    initResourcesIfNecessary();
    itemsLayout.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
        LayoutParams.WRAP_CONTENT));
    itemsLayout.measure(
        MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.UNSPECIFIED),
        MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
    int width = itemsLayout.getMeasuredWidth();

    if (mode == MeasureSpec.EXACTLY) {
      width = widthSize;
    } else {
      width += 2 * PADDING;

      // Check against our minimum width
      width = Math.max(width, getSuggestedMinimumWidth());

      if (mode == MeasureSpec.AT_MOST && widthSize < width) {
        width = widthSize;
      }
    }

    itemsLayout.measure(
        MeasureSpec.makeMeasureSpec(width - 2 * PADDING, MeasureSpec.EXACTLY),
        MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));

    return width;
  }

  /**
   * Measure the view and its content to determine teh measured witdh and the
   * measured height. This method is invoked by measure(int, int)
   * 
   * @param widthMesureSpec
   *          horizontal spece requirement imposed by the parent
   * @param heightMeasureSpec
   *          vertical space requirements as imposed by the parent. At a high
   *          level, onMesure like that: 1. the function is called ith width and
   *          height measure specifications, which should be treated
   *          requirements for the restrictions on the widt and height
   *          measurements you should produce 2. Your component's onMeasure
   *          method should calculate a measurement witdth and height which will
   *          be required to render the component. It should try to stay withing
   *          the speecifications passed in. 3. Once the width and height are
   *          calculated, then setMeasureDimension
   */
  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    int widthMode = MeasureSpec.getMode(widthMeasureSpec);
    int heightMode = MeasureSpec.getMode(heightMeasureSpec);
    int widthSize = MeasureSpec.getSize(widthMeasureSpec);
    int heightSize = MeasureSpec.getSize(heightMeasureSpec);

    buildViewForMeasuring();

    int width = calculateLayoutWidth(widthSize, widthMode);

    int height;
    if (heightMode == MeasureSpec.EXACTLY) {
      height = heightSize;
    } else {
      height = getDesiredHeight(itemsLayout);

      if (heightMode == MeasureSpec.AT_MOST) {
        height = Math.min(height, heightSize);
      }
    }

    setMeasuredDimension(width, height);
  }
  /**
   * Called when this view should assign a size and postion to all of its
   * children
   * @param changed
   *          This is a new size or position of this view
   * @param left
   *          Left position, relative to parent
   * @param top
   *          Top position, relative to parent
   * @param right
   *          Right position, relative to parent
   * @param bottom
   *          Bottom position, relative to parent
   */
  @Override
  protected void onLayout(boolean changed, int l, int t, int r, int b) {
    layout(r - l, b - t);
  }

  /**
   * Sets layouts width and height
   * 
   * @param width
   *          the layout width
   * @param height
   *          the layout height
   */
  private void layout(int width, int height) {
    int itemsWidth = width - 2 * PADDING;
    itemsLayout.layout(0, 0, itemsWidth, height);
  }

  // ------------------------------Rendering User Interface -----------
  /**
   * @param Canvas
   *          Render the interface.
   */
  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    if (viewAdapter != null && viewAdapter.getItemsCount() > 0) {
      //Rebuild view if necessary
      updateView();
      drawItems(canvas);//Draw visible items
      drawCenterRect(canvas); //Draw the focusing bar
    }
    drawShadows(canvas);
  }

  /**
   * Draws shadows on top and bottom of control 
   * 
   * @param canvas
   *          the canvas for drawing
   */
  private void drawShadows(Canvas canvas) {
    int height = (int) (1.5 * getItemHeight());
    topShadow.setBounds(0, 0, getWidth(), height);
    topShadow.draw(canvas);
    bottomShadow.setBounds(0, getHeight() - height, getWidth(), getHeight());
    bottomShadow.draw(canvas);
  }

  /**
   * Draws items
   * 
   * @param canvas
   *          the canvas for drawing
   */
  private void drawItems(Canvas canvas) {
    //push a new transformation matrix 
    //To understand this, take comp4411
    canvas.save();

    // Calculate position
    int top = (currentItem - firstItem) * getItemHeight()
        + (getItemHeight() - getHeight()) / 2;
    // Translation to proper position
    canvas.translate(PADDING, -top + scrollingOffset);
    // draw the layout
    itemsLayout.draw(canvas);

    // Restore transformation matrix
    canvas.restore();
  }

  /**
   * Draws rect for current value
   * 
   * @param canvas
   *          the canvas for drawing
   */
  private void drawCenterRect(Canvas canvas) {
    // Calculate position
    int center = getHeight() / 2;
    int offset = (int) (getItemHeight() / 2 * 1.2);
    centerDrawable.setBounds(0, center - offset, getWidth(), center + offset);
    centerDrawable.draw(canvas);
  }
  /**
   * Handle touch screen motion events
   * Normally you don't need this funciton to use this WheelView
   * Pls change the scroll listener if you really want to change 
   * the behavior
   */
  @Override
  public boolean onTouchEvent(MotionEvent event) {
    if (!isEnabled() || getViewAdapter() == null) {
      return true;
    }
    switch (event.getAction()) {
      //Nothing to do, let the parent to handle
      case MotionEvent.ACTION_MOVE:
        if (getParent() != null) {
          getParent().requestDisallowInterceptTouchEvent(true);
        }
        break;

      //Click only, no scrolling
      case MotionEvent.ACTION_UP:
        if (!isScrollingPerformed) {
          //calculate the click item position
          int distance = (int) event.getY() - getHeight() / 2;
          if (distance > 0) {
            distance += getItemHeight() / 2;
          } else {
            distance -= getItemHeight() / 2;
          }
          int items = distance / getItemHeight();
          //notify handlers to handle click
          //Normally set the clicked item focus
          //Change the ClickListener if you really want to change the
          //default behavior
          if (items != 0 && isValidItemIndex(currentItem + items)) {
            notifyClickListenersAboutClick(currentItem + items);
          }
        }
        break;
    }

    //Register the event to the scroller
    //The scroller will handle the scrolling
    return scroller.onTouchEvent(event); 
  }

  /**
   * Scrolls the wheel
   * 
   * @param delta
   *          the scrolling value
   */
  private void doScroll(int delta) {
    scrollingOffset += delta;

    int itemHeight = getItemHeight();
    int count = scrollingOffset / itemHeight;

    int pos = currentItem - count;
    int itemCount = viewAdapter.getItemsCount();

    int fixPos = scrollingOffset % itemHeight;
    if (Math.abs(fixPos) <= itemHeight / 2) {
      fixPos = 0;
    }
    if (isCyclic && itemCount > 0) {
      if (fixPos > 0) {
        pos--;
        count++;
      } else if (fixPos < 0) {
        pos++;
        count--;
      }
      // fix position by rotating
      while (pos < 0) {
        pos += itemCount;
      }
      pos %= itemCount;
    } else {
      if (pos < 0) {
        count = currentItem;
        pos = 0;
      } else if (pos >= itemCount) {
        count = currentItem - itemCount + 1;
        pos = itemCount - 1;
      } else if (pos > 0 && fixPos > 0) {
        pos--;
        count++;
      } else if (pos < itemCount - 1 && fixPos < 0) {
        pos++;
        count--;
      }
    }

    int offset = scrollingOffset;
    if (pos != currentItem) {
      setCurrentItem(pos, false);
    } else {
      invalidate();
    }

    // update offset
    scrollingOffset = offset - count * itemHeight;
    if (scrollingOffset > getHeight()) {
      scrollingOffset = scrollingOffset % getHeight() + getHeight();
    }
  }

  /**
   * Scroll the wheel
   * 
   * @param itemsToSkip
   *          items to scroll
   * @param time
   *          scrolling duration
   */
  public void scroll(int itemsToScroll, int time) {
    int distance = itemsToScroll * getItemHeight() - scrollingOffset;
    scroller.scroll(distance, time);
  }

  /**
   * Calculates range for wheel items
   * 
   * @return the items range
   */
  private ItemsRange getItemsRange() {
    if (getItemHeight() == 0) {
      return null;
    }
    // Try to find the first non-empty itme number,
    // Plus the count we get the item range

    int first = currentItem;
    int count = 1;

    // Get the invisible items, by calculating
    while (count * getItemHeight() < getHeight()) {
      first--;
      count += 2; // top + bottom items
    }

    // get invisible item number
    if (scrollingOffset != 0) {
      if (scrollingOffset > 0) {
        first--;
      }
      count++;

      // process empty items above the first or below the second
      int emptyItems = scrollingOffset / getItemHeight();
      first -= emptyItems;
      count += Math.abs(emptyItems);
    }
    return new ItemsRange(first, count);
  }

  /**
   * Rebuilds wheel items if necessary. Caches all unused items.
   * 
   * @return true if items are rebuilt
   */
  private boolean rebuildItems() {
    boolean updated = false;
    ItemsRange range = getItemsRange();
    if (itemsLayout != null && range!=null) {
      int first = recycle.recycleItems(itemsLayout, firstItem, range);
      updated = firstItem != first;
      firstItem = first;
    } else {
      createItemsLayout();
      updated = true;
    }

    if (!updated) {
      updated = firstItem != range.getFirst()
          || itemsLayout.getChildCount() != range.getCount();
    }

    if (firstItem > range.getFirst() && firstItem <= range.getLast()) {
      for (int i = firstItem - 1; i >= range.getFirst(); i--) {
        if (!addViewItem(i, true)) {
          break;
        }
        firstItem = i;
      }
    } else {
      firstItem = range.getFirst();
    }

    int first = firstItem;
    for (int i = itemsLayout.getChildCount(); i < range.getCount(); i++) {
      if (!addViewItem(firstItem + i, false)
          && itemsLayout.getChildCount() == 0) {
        first++;
      }
    }
    firstItem = first;
    return updated;
  }

  /**
   * Updates view. Rebuilds items and label if necessary, recalculate items
   * sizes.
   */
  private void updateView() {
    if (rebuildItems()) {
      calculateLayoutWidth(getWidth(), MeasureSpec.EXACTLY);
      layout(getWidth(), getHeight());
    }
  }

  /**
   * Creates item layouts if necessary
   */
  private void createItemsLayout() {
    if (itemsLayout == null) {
      itemsLayout = new LinearLayout(getContext());
      itemsLayout.setOrientation(LinearLayout.VERTICAL);
    }
  }

  /**
   * Builds view for measuring
   */
  private void buildViewForMeasuring() {
    // clear all items
    if (itemsLayout != null) {
      recycle.recycleItems(itemsLayout, firstItem, new ItemsRange());
    } else {
      createItemsLayout();
    }

    // add views
    int addItems = visibleItems / 2;
    for (int i = currentItem + addItems; i >= currentItem - addItems; i--) {
      if (addViewItem(i, true)) {
        firstItem = i;
      }
    }
  }

  /**
   * Adds view for item to items layout
   * 
   * @param index
   *          the item index
   * @param first
   *          the flag indicates if view should be first
   * @return true if corresponding item exists and is added
   */
  private boolean addViewItem(int index, boolean first) {
    View view = getItemView(index);
    if (view != null) {
      if (first) {
        itemsLayout.addView(view, 0);
      } else {
        itemsLayout.addView(view);
      }
      return true;
    }

    return false;
  }

  /**
   * Checks whether intem index is valid
   * 
   * @param index
   *          the item index
   * @return true if item index is not out of bounds or the wheel is cyclic
   */
  private boolean isValidItemIndex(int index) {
    return viewAdapter != null && viewAdapter.getItemsCount() > 0
        && (isCyclic || index >= 0 && index < viewAdapter.getItemsCount());
  }

  /**
   * Returns view for specified item
   * 
   * @param index
   *          the item index
   * @return item view or empty view if index is out of bounds
   */
  private View getItemView(int index) {
    if (viewAdapter == null || viewAdapter.getItemsCount() == 0) {
      return null; // Do nothing is the adapater is empty or not set
    }
    int count = viewAdapter.getItemsCount();
    if (!isValidItemIndex(index)) {
      return viewAdapter.getEmptyItem(recycle.getEmptyItem(), itemsLayout);
    } else {
      while (index < 0) {
        index = count + index;
      }
    }
    index %= count;
    return viewAdapter.getItem(index, recycle.getItem(), itemsLayout);
  }

  /**
   * Stops scrolling
   */
  public void stopScrolling() {
    scroller.stopScrolling();
  }
}
