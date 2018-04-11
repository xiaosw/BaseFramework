package com.xiaosw.core.aop;

import android.text.TextUtils;
import android.util.Log;

import com.xiaosw.common.util.LogUtil;
import com.xiaosw.core.aop.annotation.AutoLog;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

/**
 * @ClassName {@link AspectHandler}
 * @Description
 *
 * @Date 2018-04-10.
 * @Author xiaosw<xiaosw0802@163.com>.
 */

@Aspect
public class AspectHandler {
    
    private static final String TAG = "AspectHandler";

    private static final String LINE_BORDER = "═════════════════════════" +
            "══════════════════════════════════════════" +
            "══════════════════════════════════════════";
    private static final String LINE_DIVIDING = "─────────────────────────" +
            "──────────────────────────────────────────" +
            "──────────────────────────────────────────";


    /** 日志切点规则 */
    private static final String POINTCUT_LOG = "execution(@com.xiaosw.core.aop.annotation.AutoLog * *(..))";

    ///////////////////////////////////////////////////////////////////////////
    // log
    ///////////////////////////////////////////////////////////////////////////
    /**
     * 日志切点方法
     */
    @Pointcut(POINTCUT_LOG)
    private void log() {}

    @Around("log()")
    public Object logWeaveJoinPoint(ProceedingJoinPoint joinPoint) throws Throwable {
        if (LogUtil.getLogLevel() > Log.ERROR) {
            // disable log.
            return joinPoint.proceed();
        }
        // 组装日志信息，会有一定的效率耗损. 线上包请关闭日志。
        final long startTime = System.currentTimeMillis();

        // execute method.
        final Object result = joinPoint.proceed();
        try {
            final MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
            // Assembly args info.
            final Object[] args = joinPoint.getArgs();
            String[] parameterNames = methodSignature.getParameterNames();

            // Assembly thread and threadId info.
            final StringBuilder sb = new StringBuilder("\n\n╔").append(LINE_BORDER);
            final Thread currentThread = Thread.currentThread();
            sb.append("\n║Thread = ").append(currentThread).append(", id = ").append(currentThread.getId())
                    .append("\n║excuted: ").append(methodSignature.toString())
                    .append("\n╟").append(LINE_DIVIDING);

            int level = Log.ERROR;
            String tag = null;
            final Method method = methodSignature.getMethod();
            if (null != method && method.isAnnotationPresent(AutoLog.class)) { // 混淆后可能无法获取method
                // print log
                AutoLog autoLog = method.getAnnotation(AutoLog.class);
                tag = autoLog.tag();
                final String customMsg = autoLog.customMsg();
                if (!TextUtils.isEmpty(customMsg)) {
                    sb.append("\n║customMsg = ").append(customMsg);
                }
                level = autoLog.level();
            }

            if (null != args && args.length > 0) {
                final int argsCount =  args.length;
                sb.append("\n║args:");
                for (int i = 0; i < argsCount; i++) {
                    sb.append("\n║").append(parameterNames[i]).append(" = ").append(args[i]);
                }
            }
            sb.append("\n╟").append(LINE_DIVIDING);

            // Assembly execute result.
            sb.append("\n║execute duration = " + (System.currentTimeMillis() - startTime) + " ms.");
            final Class<?> returnType = methodSignature.getReturnType();
            LogUtil.e(TAG, "returnType = " + methodSignature.getReturnType());
            final String returnTypeSimpleName = returnType.getSimpleName();
            if (Collection.class.isAssignableFrom(returnType)) {
                sb.append("\n║result: ").append(returnTypeSimpleName).append(null == result ? " = null" : ".size() = " + ((Collection) result).size());
            } else if (Map.class.isAssignableFrom(returnType)) {
                sb.append("\n║result: ").append(returnTypeSimpleName).append(null == result ? " = null" : ".size() = " + ((Map) result).size());
            } else if (!"void".equalsIgnoreCase(returnTypeSimpleName)) {
                Object formatResult = result;
                if (isJson(formatResult)) {
                    formatResult = "\n║" + formatJsonStr((String) result);
                }
                sb.append("\n║result: ").append(returnTypeSimpleName).append(" = ").append(formatResult);
            }
            sb.append("\n╚").append(LINE_BORDER).append("\n\n");

            if (TextUtils.isEmpty(tag)) {
                tag = LogUtil.getTag();
            }
            printLog(sb, tag, level);
        } catch (Exception e) {
            LogUtil.e(TAG, "logWeaveJoinPoint: ", e);
        }
        return result;
    }

    /**
     * 输出日志
     * @param sb 日志内容
     * @param tag 日志tag
     * @param level 日志级别
     */
    private void printLog(StringBuilder sb, String tag, int level) {
        if (null == sb) {
            return;
        }
        try {
            switch (level) {
                case Log.VERBOSE:
                    LogUtil.v(tag, sb.toString());
                    break;

                case Log.DEBUG:
                    LogUtil.d(tag, sb.toString());
                    break;

                case Log.INFO:
                    LogUtil.i(tag, sb.toString());
                    break;

                case Log.WARN:
                    LogUtil.w(tag, sb.toString());
                    break;

                case Log.ERROR:
                    LogUtil.e(tag, sb.toString());
                    break;

                default:
                    LogUtil.w(tag, String.format("weaveJoinPoint: log level = %d is undefine！", level));
            }
        } catch (Exception e) {
            LogUtil.e(TAG, "printLog: ", e);
        }
    }

    /**
     * 是否为json格式
     * @param data
     * @return
     */
    private boolean isJson(Object data) {
        if (data instanceof String) {
            try {
                final String jsonData = ((String) data).trim();
                // check json format.
                if (jsonData.startsWith("[")) {
                    new JSONArray(jsonData);
                } else if (jsonData.startsWith("{")) {
                    new JSONObject((String) data);
                }
                return true;
            } catch (JSONException e) {
                // do not somthing.
            }
        }
        return false;
    }

    /**
     * 格式化json字符串
     * @param jsonStr
     * @return
     */
    private String formatJsonStr(String jsonStr) {
        jsonStr = jsonStr.replace(" ", "");
        int level = 0;
        char lastChar = ' ';
        StringBuffer jsonFormatStr = new StringBuffer();
        for(int i = 0; i< jsonStr.length(); i++){
            char c = jsonStr.charAt(i);
            if(level > 0 && '\n' == jsonFormatStr.charAt(jsonFormatStr.length()-1)){
                jsonFormatStr.append(getLevelStr(level));
            }
            switch (c) {
                case '{':
                case '[':
                    if (lastChar != ',') {
                        jsonFormatStr.append(getLevelStr(level));
                    }
                    jsonFormatStr.append(c)
                            .append("\n║");
                    level++;
                    break;

                case ',':
                    jsonFormatStr.append(c)
                            .append("\n║")
                            .append(getLevelStr(level));
                    break;

                case '}':
                case ']':
                    level--;
                    jsonFormatStr.append("\n║")
                            .append(getLevelStr(level))
                            .append(c);
                    break;

                default:
                    if (lastChar == '[' || lastChar == '{') {
                        jsonFormatStr.append(getLevelStr(level));
                    }
                    jsonFormatStr.append(c);
                    break;
            }
            lastChar = c;
        }
        return jsonFormatStr.toString();
    }

    private String getLevelStr(int level){
        StringBuffer levelStr = new StringBuffer();
        for(int lev = 0; lev < level ; lev++){
            levelStr.append("    ");
        }
        return levelStr.toString();
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    ///////////////////////////////////////////////////////////////////////////
}
