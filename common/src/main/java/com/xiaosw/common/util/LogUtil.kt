package com.xiaosw.common.util

import android.util.Log

/**
 * @ClassName: [LogUtil]
 * @Description: Log工具，类似android.util.Log。
 * tag自动产生，格式: customTagPrefix:className.methodName(L:lineNumber),
 * customTagPrefix为空时只输出：className.methodName(L:lineNumber)。
 *
 * @Date 2018-01-22.
 * @Author xiaosw<xiaosw0802></xiaosw0802>@163.com>.
 */
object LogUtil {

    /**
     * disable log print
     */
    val NONE = Integer.MAX_VALUE

    var customTag = "%s.%s:L%d"
    var mCustomTagPrefix: String? = "xiaosw-"

    var logLevel = Log.VERBOSE
        private set

    private fun getTag(): String {
        val mStackTraceElement = Thread.currentThread().stackTrace[4]
        // 全类名
        var className = mStackTraceElement.className
        className = className.substring(className.lastIndexOf(".") + 1)
        // 方法名
        val methodName = mStackTraceElement.methodName
        // 调用处所在行
        val lineNumber = mStackTraceElement.lineNumber
        customTag = String.format(customTag, className, methodName, lineNumber)
        return if (null == mCustomTagPrefix) customTag else mCustomTagPrefix!! + customTag
    }

    /**
     * @param logLevel [Log.VERBOSE] or [Log.DEBUG] or
     * [Log.INFO] or [Log.WARN] or [Log.ERROR]
     */
    fun initLogLevel(logLevel: Int) {
        this.logLevel = logLevel
    }

    fun printMsg(msg: String) {
        if (logLevel <= Log.VERBOSE) {
            println(getTag() + ":" + msg)
        }
    }

    fun printException(t: Throwable) {
        if (logLevel <= Log.ERROR) {
            t.printStackTrace()
        }
    }

    @JvmOverloads fun v(tag: String = getTag(), msg: String) {
        if (logLevel <= Log.VERBOSE) {
            Log.v(tag, msg)
        }
    }

    @JvmOverloads fun v(tag: String = getTag(), msg: String, tr: Throwable) {
        if (logLevel <= Log.VERBOSE) {
            Log.v(tag, msg, tr)
        }
    }

    @JvmOverloads fun d(tag: String = getTag(), msg: String) {
        if (logLevel <= Log.DEBUG) {
            Log.d(tag, msg)
        }
    }

    @JvmOverloads fun d(tag: String = getTag(), msg: String, tr: Throwable) {
        if (logLevel <= Log.DEBUG) {
            Log.d(tag, msg, tr)
        }
    }

    @JvmOverloads fun i(tag: String = getTag(), msg: String) {
        if (logLevel <= Log.INFO) {
            Log.i(tag, msg)
        }
    }

    @JvmOverloads fun i(tag: String = getTag(), msg: String, tr: Throwable) {
        if (logLevel <= Log.INFO) {
            Log.i(tag, msg, tr)
        }
    }

    @JvmOverloads fun w(tag: String = getTag(), msg: String) {
        if (logLevel <= Log.WARN) {
            Log.w(tag, msg)
        }
    }

    fun w(tr: Throwable) {
        if (logLevel <= Log.WARN) {
            Log.w(getTag(), tr)
        }
    }

    @JvmOverloads fun w(tag: String = getTag(), msg: String, tr: Throwable) {
        if (logLevel <= Log.WARN) {
            Log.w(tag, msg, tr)
        }
    }

    @JvmOverloads fun wtf(tag: String = getTag(), msg: String) {
        if (logLevel <= Log.WARN) {
            Log.wtf(tag, msg)
        }
    }

    @JvmOverloads fun wtf(tag: String = getTag(), msg: String, tr: Throwable) {
        if (logLevel <= Log.WARN) {
            Log.wtf(tag, msg, tr)
        }
    }

    @JvmOverloads fun e(tag: String = getTag(), msg: String) {
        if (logLevel <= Log.ERROR) {
            Log.e(tag, msg)
        }
    }

    @JvmOverloads fun e(tag: String = getTag(), msg: String, tr: Throwable) {
        if (logLevel <= Log.ERROR) {
            Log.e(tag, msg, tr)
        }
    }

}
