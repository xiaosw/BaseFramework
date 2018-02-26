package com.xiaosw.framework.config;

import com.xiaosw.framework.BuildConfig;

/**
 * @ClassName {@link AppConfig}
 * @Description
 *
 * @Date 2018-02-26.
 * @Author xiaosw<xiaosw0802@163.com>.
 */

public class AppConfig {

    public static String URL_ONLINE = "";//生产环境
    public static String URL_TEST = "http://www.baidu.com/";//测试环境
    /** 域名地址 */
    public static String URL_BASE = URL_ONLINE;
    static {
        if (BuildConfig.DEBUG) {
            URL_BASE = URL_TEST;
        }
    }

}
