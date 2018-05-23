package com.xiaosw.core.widget

import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

/**
 * @ClassName {@link ViewHolder}
 * @Description
 *
 * @Date 2018-05-23.
 * @Author xiaosw<xiaosw0802@163.com>.
 */
class ViewHolder(private val mConvertView: View, private val mPosition: Int) {

    private val mViews: SparseArray<View> by lazy {
        SparseArray<View>()
    }

    init {
        mConvertView.tag = this
    }

    fun getConvertView() = mConvertView

    fun getPosition() = mPosition

    fun <V : View> findView(viewId: Int) : V {
        var view = mViews.get(viewId)
        if (null == view) {
            view = mConvertView.findViewById(viewId)
            mViews.put(viewId, view)
        }
        return view as V
    }

    fun setText(viewId: Int, text: CharSequence) : ViewHolder {
        findView<TextView>(viewId).text = text
        return this
    }

    fun setText(viewId: Int, textId: Int) : ViewHolder {
        findView<TextView>(viewId).setText(textId)
        return this
    }

    companion object {
        fun get(inflater: LayoutInflater,
                convertView: View?,
                layoutId: Int,
                parent: ViewGroup?,
                position: Int) : ViewHolder {
            if (null == convertView) {
                return ViewHolder(inflater.inflate(layoutId, parent), position)
            }
            return convertView.tag as ViewHolder
        }
    }
}