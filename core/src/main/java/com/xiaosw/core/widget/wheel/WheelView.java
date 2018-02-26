package com.xiaosw.core.widget.wheel;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Interpolator;
import android.widget.LinearLayout;

import com.xiaosw.core.R;
import com.xiaosw.core.widget.wheel.adapter.WheelViewAdapter;

import java.util.LinkedList;
import java.util.List;

/**
 * @ClassName : {@link WheelView}
 * @Description : 滚轮选择器
 *
 * @Author xiaosw<xiaosw0802@163.com>
 * @Date 2018-01-17.
 */
public class WheelView extends View {

    /** @see WheelView#getClass().getSimpleName() */
    private static final String TAG = "WheelView";
    /**
     * Top and bottom shadows colors{0xeeffffff, 0xeaffffff, 0x33ffffff};
     */
    private static final int[] SHADOWS_COLORS = new int[]{0xddffffff, 0x99ffffff, 0x4cffffff};

    /**
     * Top and bottom mViews offset (to hide that)
     */
    private static final int ITEM_OFFSET_PERCENT = 26;

    /**
     * Left and right padding value
     */
    private static final int PADDING = 20;

    /**
     * Default mCount of visible mViews
     */
    private static final int DEF_VISIBLE_ITEMS = 5;

    // Wheel Values
    private int mCurrentItem = 0;

    // Count of visible mViews
    private int mVisibleItems = DEF_VISIBLE_ITEMS;

    // Item height
    private int mItemHeight = 0;

    // Center Line
    private Drawable mCenterDrawable;

    // Shadows drawables
    private GradientDrawable mTopShadow;
    private GradientDrawable mBottomShadow;

    // Scrolling
    private WheelViewAdapter.WheelScroller mWheelScroller;
    private boolean isScrollingPerformed;
    private int mScrollingOffset;

    // Cyclic
    boolean isCyclic = false;

    // Items layout
    public LinearLayout mLinearLayout;

    // The number of mFirst item in layout
    private int mFirstItem;

    // View adapter
    private WheelViewAdapter mWheelViewAdapter;

    // Recycle
    private WheelRecycle mRecycle = new WheelRecycle(this);

    // Listeners
    private List<OnWheelViewChangedListener> mChangingListeners =
            new LinkedList<OnWheelViewChangedListener>();
    private List<OnWheelViewScrollListener> mScrollingListeners =
            new LinkedList<OnWheelViewScrollListener>();
    private List<OnWheelViewClickedListener> mWheelViewClickingListeners =
            new LinkedList<OnWheelViewClickedListener>();

    /**
     * Constructor
     */
    public WheelView(Context context) {
        super(context);
        initData();
    }

    /**
     * Constructor
     */
    public WheelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initData();
    }

    public WheelView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initData();
    }

    /**
     * Initializes class data
     */
    private void initData() {
        mWheelScroller = new WheelViewAdapter.WheelScroller(getContext(), mScrollingListener);
    }

    WheelViewAdapter.WheelScroller.ScrollingListener mScrollingListener =
            new WheelViewAdapter.WheelScroller.ScrollingListener() {
        public void onStarted() {
            isScrollingPerformed = true;
            notifyScrollingListenersAboutStart();
        }

        public void onScroll(int distance) {
            doScroll(distance);

            int height = getHeight();
            if (mScrollingOffset > height) {
                mScrollingOffset = height;
                mWheelScroller.stopScrolling();
            } else if (mScrollingOffset < -height) {
                mScrollingOffset = -height;
                mWheelScroller.stopScrolling();
            }
        }

        @Override
        public void onFinished() {
            if (isScrollingPerformed) {
                notifyScrollingListenersAboutEnd();
                isScrollingPerformed = false;
            }

            mScrollingOffset = 0;
            invalidate();
        }

        public void onJustify() {
            if (Math.abs(mScrollingOffset) > WheelViewAdapter.WheelScroller.MIN_DELTA_FOR_SCROLLING) {
                mWheelScroller.scroll(mScrollingOffset, 0);
            }
        }
    };

    /**
     * Set the the specified scrolling interpolator
     *
     * @param interpolator the interpolator
     */
    public void setInterpolator(Interpolator interpolator) {
        mWheelScroller.setInterpolator(interpolator);
    }

    /**
     * Gets mCount of visible mViews
     *
     * @return the mCount of visible mViews
     */
    public int getVisibleItems() {
        return mVisibleItems;
    }

    /**
     * Sets the desired mCount of visible mViews. Actual amount of visible mViews
     * depends on mWheelView layout parameters. To apply changes and rebuild view
     * call measure().
     *
     * @param count the desired mCount for visible mViews
     */
    public void setVisibleItems(int count) {
        mVisibleItems = count;
    }

    /**
     * Gets view adapter
     *
     * @return the view adapter
     */
    public WheelViewAdapter getWheelViewAdapter() {
        return mWheelViewAdapter;
    }

    // Adapter listener
    private DataSetObserver mDataObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            invalidateWheel(false);
        }

        @Override
        public void onInvalidated() {
            invalidateWheel(true);
        }
    };

    /**
     * Sets view adapter. Usually new adapters contain different views, so it
     * needs to rebuild view by calling measure().
     *
     * @param wheelViewAdapter the view adapter
     */
    public void setWheelViewAdapter(WheelViewAdapter wheelViewAdapter) {
        if (this.mWheelViewAdapter != null) {
            this.mWheelViewAdapter.unregisterDataSetObserver(mDataObserver);
        }
        this.mWheelViewAdapter = wheelViewAdapter;
        if (this.mWheelViewAdapter != null) {
            this.mWheelViewAdapter.registerDataSetObserver(mDataObserver);
        }

        invalidateWheel(true);
    }

    /**
     * Adds mWheelView changing listener
     *
     * @param listener the listener
     */
    public void addWheelViewChangingListener(OnWheelViewChangedListener listener) {
        mChangingListeners.add(listener);
    }

    /**
     * Removes mWheelView changing listener
     *
     * @param listener the listener
     */
    public void removeWheelViewChangingListener(OnWheelViewChangedListener listener) {
        mChangingListeners.remove(listener);
    }

    /**
     * Notifies changing listeners
     *
     * @param oldValue the old mWheelView value
     * @param newValue the new mWheelView value
     */
    protected void notifyChangingListeners(int oldValue, int newValue) {
        for (OnWheelViewChangedListener listener : mChangingListeners) {
            listener.onChanged(this, oldValue, newValue);
        }
    }

    /**
     * Adds mWheelView scrolling listener
     *
     * @param listener the listener
     */
    public void addWheelViewScrollingListener(OnWheelViewScrollListener listener) {
        mScrollingListeners.add(listener);
    }

    /**
     * Removes mWheelView scrolling listener
     *
     * @param listener the listener
     */
    public void removeWheelViewScrollingListener(OnWheelViewScrollListener listener) {
        mScrollingListeners.remove(listener);
    }
    /**
     * Removes all mWheelView scrolling listener
     */
    public void removeAllWheelViewScrollingListener() {
        mScrollingListeners.clear();
    }

    /**
     * Notifies listeners about starting scrolling
     */
    protected void notifyScrollingListenersAboutStart() {
        for (OnWheelViewScrollListener listener : mScrollingListeners) {
            listener.onScrollingStarted(this);
        }
    }

    /**
     * Notifies listeners about ending scrolling
     */
    protected void notifyScrollingListenersAboutEnd() {
        for (OnWheelViewScrollListener listener : mScrollingListeners) {
            listener.onScrollingFinished(this);
        }
    }

    /**
     * Adds mWheelView clicking listener
     *
     * @param listener the listener
     */
    public void addWheelViewClickingListener(OnWheelViewClickedListener listener) {
        mWheelViewClickingListeners.add(listener);
    }

    /**
     * Removes mWheelView clicking listener
     *
     * @param listener the listener
     */
    public void removeWheelViewClickingListener(OnWheelViewClickedListener listener) {
        mWheelViewClickingListeners.remove(listener);
    }

    /**
     * Removes all mWheelView clicking listener
     */
    public void removeAllWheelViewClickingListener() {
        mWheelViewClickingListeners.clear();
    }

    /**
     * Notifies listeners about clicking
     */
    protected void notifyClickListenersAboutClick(int item) {
        for (OnWheelViewClickedListener listener : mWheelViewClickingListeners) {
            listener.onItemClicked(this, item);
        }
    }

    /**
     * Gets current value
     *
     * @return the current value
     */
    public int getCurrentItem() {
        return mCurrentItem;
    }

    /**
     * Sets the current item. Does nothing when index is wrong.
     *
     * @param index    the item index
     * @param animated the animation flag
     */
    public void setCurrentItem(int index, boolean animated) {
        if (mWheelViewAdapter == null || mWheelViewAdapter.getItemsCount() == 0) {
            return; // throw?
        }

        int itemCount = mWheelViewAdapter.getItemsCount();
        if (index < 0 || index >= itemCount) {
            if (isCyclic) {
                while (index < 0) {
                    index += itemCount;
                }
                index %= itemCount;
            } else {
                return; // throw?
            }
        }
        if (index != mCurrentItem) {
            if (animated) {
                int itemsToScroll = index - mCurrentItem;
                if (isCyclic) {
                    int scroll = itemCount + Math.min(index, mCurrentItem) - Math.max(index, mCurrentItem);
                    if (scroll < Math.abs(itemsToScroll)) {
                        itemsToScroll = itemsToScroll < 0 ? scroll : -scroll;
                    }
                }
                scroll(itemsToScroll, 0);
            } else {
                mScrollingOffset = 0;

                int old = mCurrentItem;
                mCurrentItem = index;

                notifyChangingListeners(old, mCurrentItem);

                invalidate();
            }
        }
    }

    /**
     * Sets the current item w/o animation. Does nothing when index is wrong.
     *
     * @param index the item index
     */
    public void setCurrentItem(int index) {
        setCurrentItem(index, false);
    }

    /**
     * Tests if mWheelView is cyclic. That means before the 1st item there is shown
     * the last one
     *
     * @return true if mWheelView is cyclic
     */
    public boolean isCyclic() {
        return isCyclic;
    }

    /**
     * Set mWheelView cyclic flag
     *
     * @param isCyclic the flag to set
     */
    public void setCyclic(boolean isCyclic) {
        this.isCyclic = isCyclic;
        invalidateWheel(false);
    }

    /**
     * Invalidates mWheelView
     *
     * @param clearCaches if true then cached views will be clear
     */
    public void invalidateWheel(boolean clearCaches) {
        if (clearCaches) {
            mRecycle.clearAll();
            if (mLinearLayout != null) {
                mLinearLayout.removeAllViews();
            }
            mScrollingOffset = 0;
        } else if (mLinearLayout != null) {
            // cache all mViews
            mRecycle.recycleItems(mLinearLayout, mFirstItem, new ItemsRange());
        }

        invalidate();
    }

    /**
     * Initializes resources
     */
    private void initResourcesIfNecessary() {
        if (mCenterDrawable == null) {
            mCenterDrawable = getContext().getResources().getDrawable(R.drawable.wheel_val);
        }

        if (mTopShadow == null) {
            mTopShadow = new GradientDrawable(Orientation.TOP_BOTTOM, SHADOWS_COLORS);
        }

        if (mBottomShadow == null) {
            mBottomShadow = new GradientDrawable(Orientation.BOTTOM_TOP, SHADOWS_COLORS);
        }

        setBackgroundResource(R.drawable.wheel_bg);
    }

    /**
     * Calculates desired height for layout
     *
     * @param layout the source layout
     * @return the desired layout height
     */
    private int getDesiredHeight(LinearLayout layout) {
        if (layout != null && layout.getChildAt(0) != null) {
            mItemHeight = layout.getChildAt(0).getMeasuredHeight();
        }

        int desired = mItemHeight * mVisibleItems - mItemHeight * ITEM_OFFSET_PERCENT / 50;

        return Math.max(desired, getSuggestedMinimumHeight());
    }

    /**
     * Returns height of mWheelView item
     *
     * @return the item height
     */
    private int getItemHeight() {
        if (mItemHeight != 0) {
            return mItemHeight;
        }

        if (mLinearLayout != null && mLinearLayout.getChildAt(0) != null) {
            mItemHeight = mLinearLayout.getChildAt(0).getHeight();
            return mItemHeight;
        }

        return getHeight() / mVisibleItems;
    }

    /**
     * Calculates control width and creates text layouts
     *
     * @param widthSize the input layout width
     * @param mode      the layout mode
     * @return the calculated control width
     */
    private int calculateLayoutWidth(int widthSize, int mode) {
        initResourcesIfNecessary();

        // TODO: make it static
        mLinearLayout.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT));
        mLinearLayout.measure(MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.UNSPECIFIED),
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        int width = mLinearLayout.getMeasuredWidth();

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

        width += width * 0.2f;
        mLinearLayout.measure(MeasureSpec.makeMeasureSpec(width - 2 * PADDING, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));

        return width;
    }

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
            height = getDesiredHeight(mLinearLayout);

            if (heightMode == MeasureSpec.AT_MOST) {
                height = Math.min(height, heightSize);
            }
        }

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        layout(r - l, b - t);
    }

    /**
     * Sets layouts width and height
     *
     * @param width  the layout width
     * @param height the layout height
     */
    private void layout(int width, int height) {
        int itemsWidth = width - 2 * PADDING;

        mLinearLayout.layout(0, 0, itemsWidth, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mWheelViewAdapter != null && mWheelViewAdapter.getItemsCount() > 0) {
            updateView();

            drawItems(canvas);
            drawCenterRect(canvas);
        }
        drawShadows(canvas);
    }

    /**
     * Draws shadows on top and bottom of control
     *
     * @param canvas the canvas for drawing
     */
    private void drawShadows(Canvas canvas) {
        int height = (int) (1.5 * getItemHeight()) + 15;
        mTopShadow.setBounds(0, 0, getWidth(), height);
        mTopShadow.draw(canvas);

        mBottomShadow.setBounds(0, getHeight() - height, getWidth(), getHeight());
        mBottomShadow.draw(canvas);
    }

    boolean drawLine = true;

    int y1 = 0;
    int y2 = 0;

    private void drawLine(Canvas canvas) {
        Paint paint = new Paint();
        // paint.setStyle(Style.ROTATE);
        paint.setColor(Color.BLACK);
//        LogUtil.i(TAG, "宽度：" + mLinearLayout.getWidth() + "    高度：" + mLinearLayout.getHeight());
        // int y3 = mLinearLayout.getWidth() / mLinearLayout.getChildCount();
        if (mLinearLayout.getChildCount() != 0) {
            if (drawLine) {
                y1 = mLinearLayout.getWidth() / 2 + (mLinearLayout.getWidth() / mLinearLayout.getChildCount()) / 4 * 3;
                y2 = mLinearLayout.getWidth() / 2 - (mLinearLayout.getWidth() / mLinearLayout.getChildCount()) / 4 * 2;
                drawLine = false;
            }
            canvas.drawLine(0, y1, getWidth(), y1, paint);
            canvas.drawLine(0, y2, getWidth(), y2, paint);
        }

    }

    /**
     * Draws mViews
     *
     * @param canvas the canvas for drawing
     */
    private void drawItems(Canvas canvas) {
        canvas.save();

        int top = (mCurrentItem - mFirstItem) * getItemHeight() + (getItemHeight() - getHeight()) / 2;
        canvas.translate(PADDING, -top + mScrollingOffset);

        mLinearLayout.draw(canvas);

        canvas.restore();
    }

    /**
     * Draws rect for current value
     *
     * @param canvas the canvas for drawing
     */
    private void drawCenterRect(Canvas canvas) {
        int center = getHeight() / 2;
        int offset = (int) (getItemHeight() / 2 * 1.2);
        mCenterDrawable.setBounds(0, center - offset, getWidth(), center + offset);
        mCenterDrawable.draw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled() || getWheelViewAdapter() == null) {
            return true;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                if (getParent() != null) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                break;

            case MotionEvent.ACTION_UP:
                if (!isScrollingPerformed) {
                    int distance = (int) event.getY() - getHeight() / 2;
                    if (distance > 0) {
                        distance += getItemHeight() / 2;
                    } else {
                        distance -= getItemHeight() / 2;
                    }
                    int items = distance / getItemHeight();
                    if (items != 0 && isValidItemIndex(mCurrentItem + items)) {
                        notifyClickListenersAboutClick(mCurrentItem + items);
                    }
                }
                break;
            default:
                break;
        }

        return mWheelScroller.onTouchEvent(event);
    }

    /**
     * Scrolls the mWheelView
     *
     * @param delta the scrolling value
     */
    private void doScroll(int delta) {
        mScrollingOffset += delta;

        int itemHeight = getItemHeight();
        int count = mScrollingOffset / itemHeight;

        int pos = mCurrentItem - count;
        int itemCount = mWheelViewAdapter.getItemsCount();

        int fixPos = mScrollingOffset % itemHeight;
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
            //
            if (pos < 0) {
                count = mCurrentItem;
                pos = 0;
            } else if (pos >= itemCount) {
                count = mCurrentItem - itemCount + 1;
                pos = itemCount - 1;
            } else if (pos > 0 && fixPos > 0) {
                pos--;
                count++;
            } else if (pos < itemCount - 1 && fixPos < 0) {
                pos++;
                count--;
            }
        }

        int offset = mScrollingOffset;
        if (pos != mCurrentItem) {
            setCurrentItem(pos, false);
        } else {
            invalidate();
        }

        // update offset
        mScrollingOffset = offset - count * itemHeight;
        if (mScrollingOffset > getHeight()) {
            mScrollingOffset = mScrollingOffset % getHeight() + getHeight();
        }
    }

    public void scroll(int itemsToScroll, int time) {
        int distance = itemsToScroll * getItemHeight() - mScrollingOffset;
        mWheelScroller.scroll(distance, time);
    }

    /**
     * Calculates range for mWheelView mViews
     *
     * @return the mViews range
     */
    private ItemsRange getItemsRange() {
        if (getItemHeight() == 0) {
            return null;
        }

        int first = mCurrentItem;
        int count = 1;

        while (count * getItemHeight() < getHeight()) {
            first--;
            count += 2; // top + bottom mViews
        }

        if (mScrollingOffset != 0) {
            if (mScrollingOffset > 0) {
                first--;
            }
            count++;

            // process empty mViews above the mFirst or below the second
            int emptyItems = mScrollingOffset / getItemHeight();
            first -= emptyItems;
            count += Math.asin(emptyItems);
        }
        return new ItemsRange(first, count);
    }

    /**
     * Rebuilds mWheelView mViews if necessary. Caches all unused mViews.
     *
     * @return true if mViews are rebuilt
     */
    private boolean rebuildItems() {
        boolean updated = false;
        ItemsRange range = getItemsRange();
        if (mLinearLayout != null) {
            int first = mRecycle.recycleItems(mLinearLayout, mFirstItem, range);
            updated = mFirstItem != first;
            mFirstItem = first;
        } else {
            createItemsLayout();
            updated = true;
        }

        if (!updated) {
            updated = mFirstItem != range.getFirst() || mLinearLayout.getChildCount() != range.getCount();
        }

        if (mFirstItem > range.getFirst() && mFirstItem <= range.getLast()) {
            for (int i = mFirstItem - 1; i >= range.getFirst(); i--) {
                if (!addViewItem(i, true)) {
                    break;
                }
                mFirstItem = i;
            }
        } else {
            mFirstItem = range.getFirst();
        }

        int first = mFirstItem;
        for (int i = mLinearLayout.getChildCount(); i < range.getCount(); i++) {
            if (!addViewItem(mFirstItem + i, false) && mLinearLayout.getChildCount() == 0) {
                first++;
            }
        }
        mFirstItem = first;

        return updated;
    }

    /**
     * Updates view. Rebuilds mViews and label if necessary, recalculate mViews
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
        if (mLinearLayout == null) {
            mLinearLayout = new LinearLayout(getContext());
            mLinearLayout.setOrientation(LinearLayout.VERTICAL);
        }
    }

    /**
     * Builds view for measuring
     */
    private void buildViewForMeasuring() {
        // clear all mViews
        if (mLinearLayout != null) {
            mRecycle.recycleItems(mLinearLayout, mFirstItem, new ItemsRange());
        } else {
            createItemsLayout();
        }

        // add views
        int addItems = mVisibleItems / 2;
        for (int i = mCurrentItem + addItems; i >= mCurrentItem - addItems; i--) {
            if (addViewItem(i, true)) {
                mFirstItem = i;
            }
        }
    }

    /**
     * Adds view for item to mViews layout
     *
     * @param index the item index
     * @param first the flag indicates if view should be mFirst
     * @return true if corresponding item exists and is added
     */
    private boolean addViewItem(int index, boolean first) {
        View view = getItemView(index);

        if (view != null) {
            if (first) {
                mLinearLayout.addView(view, 0);
            } else {
                mLinearLayout.addView(view);
            }

            return true;
        }

        return false;
    }

    /**
     * Checks whether intem index is valid
     *
     * @param index the item index
     * @return true if item index is not out of bounds or the mWheelView is cyclic
     */
    private boolean isValidItemIndex(int index) {
        return mWheelViewAdapter != null && mWheelViewAdapter.getItemsCount() > 0 &&
                (isCyclic || index >= 0 && index < mWheelViewAdapter.getItemsCount());
    }

    /**
     * Returns view for specified item
     *
     * @param index the item index
     * @return item view or empty view if index is out of bounds
     */
    private View getItemView(int index) {
        if (mWheelViewAdapter == null || mWheelViewAdapter.getItemsCount() == 0) {
            return null;
        }
        int count = mWheelViewAdapter.getItemsCount();
        if (!isValidItemIndex(index)) {
            return mWheelViewAdapter.getEmptyItem(mRecycle.getEmptyItem(), mLinearLayout);
        } else {
            while (index < 0) {
                index = count + index;
            }
        }

        index %= count;
        return mWheelViewAdapter.getItem(index, mRecycle.getItem(), mLinearLayout);
    }

    /**
     * Stops scrolling
     */
    public void stopScrolling() {
        mWheelScroller.stopScrolling();
    }

    protected class ItemsRange {
        // First item number
        private int mFirst;

        // Items mCount
        private int mCount;

        /**
         * Default constructor. Creates an empty range
         */
        public ItemsRange() {
            this(0, 0);
        }

        /**
         * Constructor
         *
         * @param first the number of mFirst item
         * @param count the mCount of mViews
         */
        public ItemsRange(int first, int count) {
            this.mFirst = first;
            this.mCount = count;
        }

        /**
         * Gets number of mFirst item
         *
         * @return the number of the mFirst item
         */
        public int getFirst() {
            return mFirst;
        }

        /**
         * Gets number of last item
         *
         * @return the number of last item
         */
        public int getLast() {
            return getFirst() + getCount() - 1;
        }

        /**
         * Get mViews mCount
         *
         * @return the mCount of mViews
         */
        public int getCount() {
            return mCount;
        }

        /**
         * Tests whether item is contained by range
         *
         * @param index the item number
         * @return true if item is contained
         */
        public boolean contains(int index) {
            return index >= getFirst() && index <= getLast();
        }
    }

    protected class WheelRecycle {
        // Cached mViews
        private List<View> mViews;

        // Cached empty mViews
        private List<View> mEmptyViews;

        // Wheel view
        private WheelView mWheelView;

        /**
         * Constructor
         *
         * @param wheel the mWheelView view
         */
        public WheelRecycle(WheelView wheel) {
            this.mWheelView = wheel;
        }

        /**
         * Recycles mViews from specified layout. There are saved only mViews not
         * included to specified range. All the cached mViews are removed from
         * original layout.
         *
         * @param layout    the layout containing mViews to be cached
         * @param firstItem the number of mFirst item in layout
         * @param range     the range of current mWheelView mViews
         * @return the new value of mFirst item number
         */
        public int recycleItems(LinearLayout layout, int firstItem, ItemsRange range) {
            int index = firstItem;
            for (int i = 0; i < layout.getChildCount(); ) {
                if (!range.contains(index)) {
                    recycleView(layout.getChildAt(i), index);
                    layout.removeViewAt(i);
                    if (i == 0) { // mFirst item
                        firstItem++;
                    }
                } else {
                    i++; // go to next item
                }
                index++;
            }
            return firstItem;
        }

        /**
         * Gets item view
         *
         * @return the cached view
         */
        public View getItem() {
            return getCachedView(mViews);
        }

        /**
         * Gets empty item view
         *
         * @return the cached empty view
         */
        public View getEmptyItem() {
            return getCachedView(mEmptyViews);
        }

        /**
         * Clears all views
         */
        public void clearAll() {
            if (mViews != null) {
                mViews.clear();
            }
            if (mEmptyViews != null) {
                mEmptyViews.clear();
            }
        }

        /**
         * Adds view to specified cache. Creates a cache list if it is null.
         *
         * @param view  the view to be cached
         * @param cache the cache list
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
         * Adds view to cache. Determines view type (item view or empty one) by
         * index.
         *
         * @param view  the view to be cached
         * @param index the index of view
         */
        private void recycleView(View view, int index) {
            int count = mWheelView.getWheelViewAdapter().getItemsCount();

            if ((index < 0 || index >= count) && !mWheelView.isCyclic()) {
                // empty view
                mEmptyViews = addView(view, mEmptyViews);
            } else {
                while (index < 0) {
                    index = count + index;
                }
                index %= count;
                mViews = addView(view, mViews);
            }
        }

        /**
         * Gets view from specified cache.
         *
         * @param cache the cache
         * @return the mFirst view from cache.
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

    ///////////////////////////////////////////////////////////////////////////
    // listeners
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Wheel clicked mListener interface.
     * <p/>
     * The onItemClicked() method is called whenever a mWheelView item is clicked
     * <li>New Wheel position is set
     * <li>Wheel view is scrolled
     */
    public interface OnWheelViewClickedListener {
        /**
         * Callback method to be invoked when current item clicked
         *
         * @param wheel     the mWheelView view
         * @param itemIndex the index of clicked item
         */
        void onItemClicked(WheelView wheel, int itemIndex);

    }

    public interface OnWheelViewChangedListener {
        /**
         * Callback method to be invoked when current item changed
         *
         * @param wheel    the mWheelView view whose state has changed
         * @param oldValue the old value of current item
         * @param newValue the new value of current item
         */
        void onChanged(WheelView wheel, int oldValue, int newValue);
    }

    public interface OnWheelViewScrollListener {
        /**
         * Callback method to be invoked when scrolling started.
         *
         * @param wheel the mWheelView view whose state has changed.
         */
        void onScrollingStarted(WheelView wheel);

        /**
         * Callback method to be invoked when scrolling ended.
         *
         * @param wheel the mWheelView view whose state has changed.
         */
        void onScrollingFinished(WheelView wheel);
    }

}


