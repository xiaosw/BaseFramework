package com.xiaosw.core.widget.wheel.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import static android.R.attr.textSize;

/**
 * <p><br/>ClassName : {@link AbstractWheelTextAdapter}
 * <br/>Description :
 * <br/>
 * <br/>Author : xiaosw<xiaosw0802@163.com>
 * <br/>Create date : 2018-01-17</p>
 */

public abstract class AbstractWheelTextAdapter extends AbstractWheelAdapter {

    /** @see AbstractWheelTextAdapter#getClass().getSimpleName() */
    private static final String TAG = "PTAbstractWheelTextAdapter";

    /**
     * Text view resource. Used as a default view for mWheelAdapter.
     */
    public static final int TEXT_VIEW_ITEM_RESOURCE = -1;

    /**
     * No resource constant.
     */
    protected static final int NO_RESOURCE = 0;

    /**
     * Default text color
     */
    public static final int DEFAULT_TEXT_COLOR = 0xFF101010;

    /**
     * Default text color
     */
    public static final int LABEL_COLOR = 0xFF700070;

    /**
     * Default text size
     */
    public static final int DEFAULT_SELECTED_TEXT_SIZE = 24;
    public static final int DEFAULT_NORMAL_TEXT_SIZE = 14;

    // Text settings
    private int textColor = DEFAULT_TEXT_COLOR;

    // Current context
    protected Context context;
    // Layout inflater
    protected LayoutInflater inflater;

    // Items resources
    protected int itemResourceId;
    protected int itemTextResourceId;

    // Empty items resources
    protected int emptyItemResourceId;

    private int currentIndex = 0;
    private int mSelectedTextSize = DEFAULT_SELECTED_TEXT_SIZE;
    private int mDefaultTextSize = DEFAULT_NORMAL_TEXT_SIZE;
    private ArrayList<View> arrayList = new ArrayList<View>();

    /**
     * Constructor
     *
     * @param context the current context
     */
    protected AbstractWheelTextAdapter(Context context) {
        this(context, TEXT_VIEW_ITEM_RESOURCE);
    }

    /**
     * Constructor
     *
     * @param context      the current context
     * @param itemResource the resource ID for a layout file containing a TextView to use
     *                     when instantiating items views
     */
    protected AbstractWheelTextAdapter(Context context, int itemResource) {
        this(context, itemResource, NO_RESOURCE, 0, DEFAULT_SELECTED_TEXT_SIZE, DEFAULT_NORMAL_TEXT_SIZE);
    }

    /**
     * Constructor
     *
     * @param context          the current context
     * @param itemResource     the resource ID for a layout file containing a TextView to use
     *                         when instantiating items views
     * @param itemTextResource the resource ID for a text view in the item layout
     */
    protected AbstractWheelTextAdapter(Context context, int itemResource, int itemTextResource, int currentIndex,
                                       int maxsize, int minsize) {
        this.context = context;
        itemResourceId = itemResource;
        itemTextResourceId = itemTextResource;
        this.currentIndex = currentIndex;
        this.mSelectedTextSize = maxsize;
        this.mDefaultTextSize = minsize;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    /**
     * get the mDateDatas of show textview
     *
     * @return the array of textview
     */
    public ArrayList<View> getTestViews() {
        return arrayList;
    }

    /**
     * Gets text color
     *
     * @return the text color
     */
    public int getTextColor() {
        return textColor;
    }

    /**
     * Sets text color
     *
     * @param textColor the text color to set
     */
    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    /**
     * Gets text size
     *
     * @return the text size
     */
    public int getSelectedTextSize() {
        return mSelectedTextSize;
    }

    /**
     * Sets text size
     *
     * @param selectedTextSize the text size to set
     */
    public void setSelectedTextSize(int selectedTextSize) {
        this.mSelectedTextSize = selectedTextSize;
    }

    public int getDefaultTextSize() {
        return mDefaultTextSize;
    }

    public void setDefaultTextSize(int defaultTextSize) {
        mDefaultTextSize = defaultTextSize;
    }

    /**
     * Gets resource Id for items views
     *
     * @return the item resource Id
     */
    public int getItemResource() {
        return itemResourceId;
    }

    /**
     * Sets resource Id for items views
     *
     * @param itemResourceId the resource Id to set
     */
    public void setItemResource(int itemResourceId) {
        this.itemResourceId = itemResourceId;
    }

    /**
     * Gets resource Id for text view in item layout
     *
     * @return the item text resource Id
     */
    public int getItemTextResource() {
        return itemTextResourceId;
    }

    /**
     * Sets resource Id for text view in item layout
     *
     * @param itemTextResourceId the item text resource Id to set
     */
    public void setItemTextResource(int itemTextResourceId) {
        this.itemTextResourceId = itemTextResourceId;
    }

    /**
     * Gets resource Id for empty items views
     *
     * @return the empty item resource Id
     */
    public int getEmptyItemResource() {
        return emptyItemResourceId;
    }

    /**
     * Sets resource Id for empty items views
     *
     * @param emptyItemResourceId the empty item resource Id to set
     */
    public void setEmptyItemResource(int emptyItemResourceId) {
        this.emptyItemResourceId = emptyItemResourceId;
    }

    /**
     * Returns text for specified item
     *
     * @param index the item index
     * @return the text of specified items
     */
    protected abstract CharSequence getItemText(int index);

    @Override
    public View getItem(int index, View convertView, ViewGroup parent) {
        if (index >= 0 && index < getItemsCount()) {
            if (convertView == null) {
                convertView = getView(itemResourceId, parent);
            }
            TextView textView = getTextView(convertView, itemTextResourceId);
            if (!arrayList.contains(textView)) {
                arrayList.add(textView);
            }
            if (textView != null) {
                CharSequence text = getItemText(index);
                if (text == null) {
                    text = "";
                }
                textView.setText(text);
                // LogUtil.i(TAG, "index:" + index + "     currentIndex:" + currentIndex);
                if (index == currentIndex) {
                    textView.setTextSize(mSelectedTextSize);
                } else {
                    textView.setTextSize(mDefaultTextSize);
                }

                if (itemResourceId == TEXT_VIEW_ITEM_RESOURCE) {
                    configureTextView(textView);
                }
            }
            return convertView;
        }
        return null;
    }

    @Override
    public View getEmptyItem(View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = getView(emptyItemResourceId, parent);
        }
        if (emptyItemResourceId == TEXT_VIEW_ITEM_RESOURCE && convertView instanceof TextView) {
            configureTextView((TextView) convertView);
        }

        return convertView;
    }

    /**
     * Configures text view. Is called for the TEXT_VIEW_ITEM_RESOURCE views.
     *
     * @param view the text view to be configured
     */
    protected void configureTextView(TextView view) {
        view.setTextColor(textColor);
        view.setGravity(Gravity.CENTER);
        view.setTextSize(textSize);
        view.setLines(1);
        view.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
    }

    /**
     * Loads a text view from view
     *
     * @param view         the text view or layout containing it
     * @param textResource the text resource Id in layout
     * @return the loaded text view
     */
    private TextView getTextView(View view, int textResource) {
        TextView text = null;
        try {
            if (textResource == NO_RESOURCE && view instanceof TextView) {
                text = (TextView) view;
            } else if (textResource != NO_RESOURCE) {
                text = (TextView) view.findViewById(textResource);
            }
        } catch (ClassCastException e) {
            Log.e("AbstractWheelAdapter", "You must supply a resource ID for a TextView");
            throw new IllegalStateException("AbstractWheelAdapter requires the resource ID to be a TextView", e);
        }

        return text;
    }

    /**
     * Loads view from resources
     *
     * @param resource the resource Id
     * @return the loaded view or null if resource is not set
     */
    private View getView(int resource, ViewGroup parent) {
        switch (resource) {
            case NO_RESOURCE:
                return null;
            case TEXT_VIEW_ITEM_RESOURCE:
                return new TextView(context);
            default:
                return inflater.inflate(resource, parent, false);
        }
    }

    /**
     * 设置字体大小
     */
    public String updateTextStyle(int position) {
        ArrayList<View> arrayList = getTestViews();
        int size = arrayList.size();
        String currentDateText = getItemText(position).toString().trim();
        if (TextUtils.isEmpty(currentDateText)) {
            return null;
        }
        for (int i = 0; i < size; i++) {
            TextView textvew = (TextView) arrayList.get(i);
            if (textvew.getText().toString().equals(currentDateText)) {
                textvew.setTextSize(mSelectedTextSize);
            } else {
                textvew.setTextSize(mDefaultTextSize);
            }
        }
        return currentDateText;
    }

}
