package com.xiaosw.common.manager

import android.util.Log
import com.xiaosw.common.util.LogUtil
import okhttp3.logging.HttpLoggingInterceptor

/**
 * @ClassName [GlobalManager]
 * @Description
 *
 * @Date 2018-02-06.
 * @Author xiaosw<xiaosw0802></xiaosw0802>@163.com>.
 */

class GlobalManager {

    companion object Config {
        var isDebug: Boolean = false
            set(value) {
                field = value
                if(field) {
                    LogUtil.initLogLevel(Log.VERBOSE)
                } else {
                    LogUtil.initLogLevel(LogUtil.NONE)
                }
            }
    }

}
