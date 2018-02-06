package com.xiaosw.common.util

import android.app.Application
import android.content.Context
import kotlin.properties.Delegates

/**
 * @ClassName [AndroidContext]
 * @Description
 *
 * @Date 2018-02-02.
 * @Author xiaosw<xiaosw0802></xiaosw0802>@163.com>.
 */

object AndroidContext {

    private var sApplication: Application by Delegates.notNull()

    /**
     * must call in [Application.onCreate] or [Application.attachBaseContext]
     * @param app
     */
    fun init(app: Application) {
        sApplication = app
        NetworkStatusHelper.initNetworkStatus()
    }

    fun get(): Context {
        return sApplication
    }

}
