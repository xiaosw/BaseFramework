package com.xiaosw.common.util

import com.google.gson.Gson

import java.lang.ref.SoftReference

/**
 * @ClassName [GsonUtils]
 * @Description
 * @Date 2018-02-26.
 * @Author xiaosw<xiaosw0802></xiaosw0802>@163.com>.
 */

object GsonUtils {

    private var sGosnRef: SoftReference<Gson>? = null

    private val gson: Gson
        @Synchronized get() {
            if (null == sGosnRef || sGosnRef!!.get() == null) {
                sGosnRef = null
                sGosnRef = SoftReference(Gson())
            }
            return sGosnRef!!.get()!!
        }

    fun <T> fromJson(result: String, clazz: Class<T>): T {
        return gson.fromJson(result, clazz)
    }

    fun <T> toJson(t: T): String {
        return gson.toJson(t)
    }
}
