package com.xiaosw.core.aop;

import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

import com.xiaosw.common.util.LogUtil;
import com.xiaosw.core.aop.annotation.AutoLog;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

/**
 * @ClassName {@link AopJoinPoint}
 * @Description
 *
 * @Date 2018-04-10.
 * @Author xiaosw<xiaosw0802@163.com>.
 */

@Aspect
public class AopJoinPoint {

    private static final String LINE = "════════════════════════════════════════════════════════════════════════════════";

    @Pointcut("execution(@com.xiaosw.core.aop.annotation.AutoLog * *(..))")
    private void autoLog() {

    }

    @Around("autoLog()")
    public Object logWeaveJoinPoint(ProceedingJoinPoint joinPoint) throws Throwable {
        if (LogUtil.getLogLevel() > Log.ERROR) {
            // release model. not print log.
            return joinPoint.proceed();
        }
        final long startTime = System.currentTimeMillis();
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        Object result = joinPoint.proceed();
        if (method.isAnnotationPresent(AutoLog.class)) {
            // print log
            AutoLog autoLog = method.getAnnotation(AutoLog.class);
            String tag = autoLog.tag();
            if (TextUtils.isEmpty(tag)) {
                tag = LogUtil.getTag();
            }
            StringBuilder sb = new StringBuilder("\n\n╔").append(LINE);
            final Thread currentThread = Thread.currentThread();
            sb.append("\n║Thread = ").append(currentThread).append(", id = ").append(currentThread.getId())
              .append("\n║excuted: ").append(methodSignature.toString());
            final Object[] args = joinPoint.getArgs();
            if (null != args && args.length > 0) {
                final int argsCount =  args.length;
                String[] parameterNames = methodSignature.getParameterNames();
                sb.append("\n║").append(LINE)
                .append("\n║args:");
                for (int i = 0; i < argsCount; i++) {
                    sb.append("\n║").append(parameterNames[i]).append(" = ").append(args[i]);
                }
                sb.append("\n║").append(LINE);
            } else {
                sb.append("\n║").append(LINE);
            }
            sb.append("\n║excute duration = " + (System.currentTimeMillis() - startTime) + " ms.");
            final Class<?> returnType = method.getReturnType();
            final String returnTypeSimpleName = returnType.getSimpleName();
            if (Collection.class.isAssignableFrom(returnType)) {
                sb.append("\n║result: ").append(returnTypeSimpleName).append(null == result ? " = null" : ".size() = " + ((Collection) result).size());
            } else if (Map.class.isAssignableFrom(returnType)) {
                sb.append("\n║result: ").append(returnTypeSimpleName).append(null == result ? " = null" : ".size() = " + ((Map) result).size());
            } else if (!"void".equalsIgnoreCase(returnTypeSimpleName)) {
                sb.append("\n║result: ").append(returnTypeSimpleName).append(" = ").append(result);
            }
            sb.append("\n╚").append(LINE).append("\n\n");

            int level = autoLog.level();
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
        }
        return result;
    }

}
