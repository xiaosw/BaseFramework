package com.xiaosw.core.widget.wheel.adapter;

import android.content.Context;

/**
 * <p><br/>ClassName : {@link ArrayWheelAdapter}
 * <br/>Description :
 * <br/>
 * <br/>Author : xiaosw<xiaosw0802@163.com>
 * <br/>Create date : 2018-01-17</p>
 */

public class ArrayWheelAdapter<T> extends AbstractWheelTextAdapter {

    // mDatas
    private T[] mDatas;

    public ArrayWheelAdapter(Context context, T[] datas) {
        super(context);
        // setEmptyItemResource(TEXT_VIEW_ITEM_RESOURCE);
        this.mDatas = datas;
    }

    @Override
    public CharSequence getItemText(int index) {
        if (index >= 0 && index < mDatas.length) {
            T item = mDatas[index];
            if (item instanceof CharSequence) {
                return (CharSequence) item;
            }
            return item.toString();
        }
        return null;
    }

    @Override
    public int getItemsCount() {
        return mDatas.length;
    }

}
