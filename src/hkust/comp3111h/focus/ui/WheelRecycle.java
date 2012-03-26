package hkust.comp3111h.focus.ui;

import java.util.LinkedList;
import java.util.List;

import android.view.View;
import android.widget.LinearLayout;

/**
 * Recycle stores wheel items to reuse For efficiency propose Basically work as
 * a stack, every time the every time push the items in the offset into it, and
 * pop whenever we need one
 */

public class WheelRecycle {
  // Cached items
  private List<View> items;
  // Cached empty items
  private List<View> emptyItems;
  // Wheel View
  private WheelView wheel;

  /**
   * Constructor
   * 
   * @param wheel
   *          teh wheel view
   */
  public WheelRecycle(WheelView wheel) {
    this.wheel = wheel;
  }

  /**
   * Recycles items from specified layout There are saved only items not
   * included to specified range. All teh cached items are removed from original
   * layout
   * 
   * @param layout
   *          the layout containing items to be cached
   * @param firstItem
   *          the number of first item in layout
   * @param range
   *          teh range of current items
   * @param teh
   *          new value of the first item number
   */
  public int recycleItems(LinearLayout layout, int firstItem, ItemsRange range) {
    int index = firstItem;
    for (int i = 0; i < layout.getChildCount();) {
      if (!range.contains(index)) {
        recycleView(layout.getChildAt(i), index);
        layout.removeViewAt(i);
        if (i == 0) {
          firstItem++;
        }
      } else {
        i++;
      }
      index++;
    }
    return firstItem;
  }

  /**
   * Get item view
   * 
   * @return the cached view
   */
  public View getItem() {
    return getCachedView(items);
  }

  /**
   * Gets empty item view
   * 
   * @return the cached empty view
   */
  public View getEmptyItem() {
    return getCachedView(emptyItems);
  }

  /**
   * Clears all views
   */
  public void clearAll() {
    if (items != null) {
      items.clear();
    }
    if (emptyItems != null) {
      emptyItems.clear();
    }
  }

  /**
   * Adds view to specified cache. Creates a cache list if it is null.
   * 
   * @param view
   *          the view to be cached
   * @param cache
   *          the cache list
   * @return the cache list
   */
  private List<View> addView(View view, List<View> cache) {
    if (cache == null) {
      cache = new LinkedList<View>();
    }

    cache.add(view);
    return cache;
  }

  /**
   * Adds view to cache. Determines view type (items view or empty one) by index
   * 
   * @param view
   *          the view to be cached
   * @param index
   *          the index of view
   */
  private void recycleView(View view, int index) {
    int count = wheel.getViewAdapter().getItemsCount();

    if ((index < 0 || index >= count) && !wheel.isCyclic()) {
      // empty view
      emptyItems = addView(view, emptyItems);
    } else {
      while (index < 0) {
        index = count + index;
      }
      index %= count;
      items = addView(view, items);
    }
  }

  /**
   * Gets view from specified cached, pop the first top one
   * 
   * @param cache
   *          the cache
   * @return the first view from cache
   */
  private View getCachedView(List<View> cache) {
    if (cache != null && cache.size() > 0) {
      View view = cache.get(0);
      cache.remove(0);
      return view;
    }
    return null;
  }
}
