package com.xiaosw.core.aop.annotation;

import android.util.Log;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @ClassName {@link AutoLog}
 * @Description 自动输出日志
 *
 * @Date 2018-04-10.
 * @Author xiaosw<xiaosw0802@163.com>.
 */

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
public @interface AutoLog {

    String tag() default "xiaosw";

    /**
     * Log level
     * @return {@link Log#VERBOSE} or {@link Log#DEBUG} or {@link Log#INFO} or {@link Log#WARN} or
     * {@link Log#ERROR}. default is {@link Log#ERROR}
     */
    int level() default Log.ERROR;

}
