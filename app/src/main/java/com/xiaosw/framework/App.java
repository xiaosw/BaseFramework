package com.xiaosw.framework;

import com.xiaosw.common.manager.GlobalManager;
import com.xiaosw.common.util.LogUtil;
import com.xiaosw.core.BaseApplication;


/**
 * @ClassName {@link App}
 * @Description
 * @Date 2018-02-06.
 * @Author xiaosw<xiaosw0802@163.com>.
 */

public class App extends BaseApplication {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected boolean useMultiDex() {
        return true;
    }

    @Override
    protected boolean isDebug() {
        return BuildConfig.DEBUG;
    }
}
