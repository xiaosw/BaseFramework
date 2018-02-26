package com.xiaosw.core.widget.wheel.adapter;

import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;

import java.util.LinkedList;
import java.util.List;

/**
 * <p><br/>ClassName : {@link AbstractWheelAdapter}
 * <br/>Description : Abstract WheelViewAdapter.
 * <br/>
 * <br/>Author : xiaosw<xiaosw0802@163.com>
 * <br/>Create date : 2018-01-17</p>
 */

public abstract class AbstractWheelAdapter implements WheelViewAdapter {

    // Observers
    private List<DataSetObserver> mDataSetObservers;

    @Override
    public View getEmptyItem(View convertView, ViewGroup parent) {
        return null;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        if (mDataSetObservers == null) {
            mDataSetObservers = new LinkedList<DataSetObserver>();
        }
        mDataSetObservers.add(observer);
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        if (mDataSetObservers != null) {
            mDataSetObservers.remove(observer);
        }
    }

    /**
     * Notifies observers about data changing
     */
    protected void notifyDataChangedEvent() {
        if (mDataSetObservers != null) {
            for (DataSetObserver observer : mDataSetObservers) {
                observer.onChanged();
            }
        }
    }

    /**
     * Notifies observers about invalidating data
     */
    protected void notifyDataInvalidatedEvent() {
        if (mDataSetObservers != null) {
            for (DataSetObserver observer : mDataSetObservers) {
                observer.onInvalidated();
            }
        }
    }

    @Override
    public void clearDataSetObserver() {
        if (mDataSetObservers != null) {
            mDataSetObservers.clear();
        }
    }
}
