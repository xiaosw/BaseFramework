package com.xiaosw.core.aop.annotation;

import android.util.Log;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.xiaosw.common.util.LogUtil;

/**
 * @ClassName {@link AutoLog}
 * @Description 自动输出log注解。调试会有一定的效率耗损，关闭日志后无影响：
 * {@link LogUtil#initLogLevel(int)} use level {@link LogUtil#NONE}.
 *
 * @Date 2018-04-10.
 * @Author xiaosw<xiaosw0802@163.com>.
 */

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
public @interface AutoLog {

    /**
     * 日志tag
     * @return
     */
    String tag() default "xiaosw-doit";

    /**
     * 日志级别 {@link Log#VERBOSE} or {@link Log#DEBUG} or {@link Log#INFO} or {@link Log#WARN} or
     * {@link Log#ERROR}. default is {@link Log#ERROR}
     *
     * @return
     */
    int level() default Log.ERROR;

    /**
     * 自定义消息
     * @return
     */
    String customMsg() default "";
}
