package com.xiaosw.core.widget

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import kotlin.properties.Delegates

/**
 * @ClassName [UniversalBaseAdapter]
 * @Description
 *
 * @Date 2018-05-23.
 * @Author xiaosw<xiaosw0802@163.com>.
 */
open abstract class UniversalBaseAdapter<T> @JvmOverloads constructor (
        private val mContext: Context,
        private val mItemLayoutId: Int = -1,
        private val mData: MutableList<T> = mutableListOf()
) : BaseAdapter() {

    private val mInflater by lazy {
        LayoutInflater.from(mContext)
    }

    private var mCurrentConvertView: View by Delegates.notNull()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val viewHolder = ViewHolder.get(mInflater, convertView, mItemLayoutId,
                parent, position)
        mCurrentConvertView = viewHolder.getConvertView()
        bindView(viewHolder, getItem(position), position)
        return mCurrentConvertView
    }

    override fun getItem(position: Int) = mData[position]

    override fun getItemId(position: Int) = position.toLong()

    override fun getCount() = mData.size

    abstract fun bindView(viewHolder: ViewHolder, data: T, position: Int)

    fun getCotext() = mContext

    fun getData() = mData

    /**
     * local refresh.
     */
    fun update(listView: ListView, position: Int) {
        // compile index in listView
        val firstVisiblePosition = listView.firstVisiblePosition
        if (position >= firstVisiblePosition && position <= listView.lastVisiblePosition) {
            val childView = listView.getChildAt(position - firstVisiblePosition)
            // reset view
            bindView(childView.tag as ViewHolder, mData[position], position)
        }
    }

    @Synchronized fun refresh(newData: MutableList<T>) {
        mData.clear()
        mData.addAll(newData)
        notifyDataSetChanged()
    }

    @Synchronized fun addAll(datas: MutableList<T>) {
        if (null == datas || datas.size == 0) {
            // prevent call from java exception.
            return
        }
        mData.addAll(datas)
        notifyDataSetChanged()
    }

    @Synchronized fun add(data: T) {
        mData.add(data)
        notifyDataSetChanged()
    }

    @Synchronized fun clear() {
        mData.clear()
        notifyDataSetChanged()
    }

    fun setText(viewId: Int, text: CharSequence) {
        (mCurrentConvertView.tag as ViewHolder).apply {
            setText(viewId, text)
        }
    }

    fun setText(viewId: Int, textId: Int) {
        (mCurrentConvertView.tag as ViewHolder).apply {
            setText(viewId, textId)
        }
    }

}