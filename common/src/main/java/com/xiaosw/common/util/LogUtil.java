package com.xiaosw.common.util;

import android.util.Log;

/**
 * @ClassName: {@link LogUtil}
 * @Description: Log工具，类似android.util.Log。
 * tag自动产生，格式: customTagPrefix:className.methodName(L:lineNumber),
 * customTagPrefix为空时只输出：className.methodName(L:lineNumber)。
 *
 * @Date 2018-01-22.
 * @Author xiaosw<xiaosw0802@163.com>.
 */
public class LogUtil {

    /**
     * disable log print
     */
    public static final int NONE = Integer.MAX_VALUE;

    public static String tag = "%s.%s:L%d";
    public static String mCustomTagPrefix = "xiaosw-";

    private static int mLogLevel = Log.VERBOSE;

    private static String getTag() {
        StackTraceElement mStackTraceElement = Thread.currentThread().getStackTrace()[4];
        // 全类名
        String className = mStackTraceElement.getClassName();
        className = className.substring(className.lastIndexOf(".") + 1);
        // 方法名
        String methodName = mStackTraceElement.getMethodName();
        // 调用处所在行
        int lineNumber = mStackTraceElement.getLineNumber();
        tag = String.format(tag, className, methodName, lineNumber);
        return null == mCustomTagPrefix ? tag : mCustomTagPrefix + tag;
    }

    /**
     * @param logLevel {@link Log#VERBOSE} or {@link Log#DEBUG} or
     * {@link Log#INFO} or {@link Log#WARN} or {@link Log#ERROR}
     */
    public static void initLogLevel(int logLevel) {
        mLogLevel = logLevel;
    }

    public static int getLogLevel() {
        return mLogLevel;
    }

    public static void printMsg(String msg) {
        if(mLogLevel <= Log.VERBOSE) {
            System.out.println(getTag() + ":" + msg);
        }
    }

    public static void printException(Throwable t) {
        if(mLogLevel <= Log.ERROR) {
            t.printStackTrace();
        }
    }

    public static void v(String msg) {
        if(mLogLevel <= Log.VERBOSE) {
            v(getTag(), msg);
        }
    }

    public static void v(String tag, String msg) {
        if(mLogLevel <= Log.VERBOSE) {
            Log.v(tag, msg);
        }
    }

    public static void v(String msg, Throwable tr) {
        if (mLogLevel <= Log.VERBOSE) {
            v(getTag(), msg, tr);
        }
    }

    public static void v(String tag, String msg, Throwable tr) {
        if(mLogLevel <= Log.VERBOSE) {
            Log.v(tag, msg, tr);
        }
    }

    public static void d(String msg) {
        if (mLogLevel <= Log.DEBUG) {
            d(getTag(), msg);
        }
    }

    public static void d(String tag, String msg) {
        if(mLogLevel <= Log.DEBUG) {
            Log.d(tag, msg);
        }
    }

    public static void d(String msg, Throwable tr) {
        if (mLogLevel <= Log.DEBUG) {
            d(getTag(), msg, tr);
        }
    }

    public static void d(String tag, String msg, Throwable tr) {
        if(mLogLevel <= Log.DEBUG) {
            Log.d(tag, msg, tr);
        }
    }

    public static void i(String msg) {
        if (mLogLevel <= Log.INFO) {
            i(getTag(), msg);
        }
    }

    public static void i(String tag, String msg) {
        if(mLogLevel <= Log.INFO) {
            Log.i(tag, msg);
        }
    }

    public static void i(String msg, Throwable tr) {
        if (mLogLevel <= Log.INFO) {
            i(getTag(), msg, tr);
        }
    }

    public static void i(String tag, String msg, Throwable tr) {
        if(mLogLevel <= Log.INFO) {
            Log.i(tag, msg, tr);
        }
    }

    public static void w(String msg) {
        if (mLogLevel <= Log.WARN) {
            w(getTag(), msg);
        }
    }

    public static void w(String tag, String msg) {
        if(mLogLevel <= Log.WARN) {
            Log.w(tag, msg);
        }
    }

    public static void w(Throwable tr) {
        if(mLogLevel <= Log.WARN) {
            Log.w(getTag(), tr);
        }
    }

    public static void w(String msg, Throwable tr) {
        if (mLogLevel <= Log.WARN) {
            w(getTag(), msg, tr);
        }
    }

    public static void w(String tag, String msg, Throwable tr) {
        if(mLogLevel <= Log.WARN) {
            Log.w(tag, msg, tr);
        }
    }

    public static void wtf(String msg) {
        if (mLogLevel <= Log.WARN) {
            wtf(getTag(), msg);
        }
    }

    public static void wtf(String tag, String msg) {
        if(mLogLevel <= Log.WARN) {
            Log.wtf(tag, msg);
        }
    }

    public static void wtf(Throwable tr) {
        if(mLogLevel <= Log.WARN) {
            Log.wtf(getTag(), tr);
        }
    }

    public static void wtf(String tag, Throwable tr) {
        if (mLogLevel <= Log.WARN) {
            wtf(tag, tr);
        }
    }

    public static void wtf(String tag, String msg, Throwable tr) {
        if(mLogLevel <= Log.WARN) {
            Log.wtf(tag, msg, tr);
        }
    }

    public static void e(String msg) {
        if (mLogLevel <= Log.ERROR) {
            e(getTag(), msg);
        }
    }

    public static void e(String tag, String msg) {
        if(mLogLevel <= Log.ERROR) {
            Log.e(tag, msg);
        }
    }

    public static void e(String msg, Throwable tr) {
        if (mLogLevel <= Log.ERROR) {
            e(getTag(), msg, tr);
        }
    }

    public static void e(String tag, String msg, Throwable tr) {
        if(mLogLevel <= Log.ERROR) {
            Log.e(tag, msg, tr);
        }
    }

    private LogUtil() {
    }

}
