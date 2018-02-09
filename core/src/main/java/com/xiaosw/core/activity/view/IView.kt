package com.xiaosw.core.activity.view

import android.content.Context

/**
 * @ClassName {@link IView}
 * @Description
 *
 * @Date 2018-02-09.
 * @Author xiaosw<xiaosw0802@163.com>.
 */
interface IView {

    /**
     * Activity or Framgent or Dialog
     */
    fun getContext() : Context

    /**
     * Application Context
     */
    fun getAppContext() : Context

    fun showLoading()

    fun dissmissLoading()

}