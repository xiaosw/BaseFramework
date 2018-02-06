package com.xiaosw.framework;

import com.xiaosw.core.BaseApplication;

/**
 * @ClassName {@link App}
 * @Description
 * @Date 2018-02-06.
 * @Author xiaosw<xiaosw0802@163.com>.
 */

public class App extends BaseApplication {

    @Override
    protected boolean useMultiDex() {
        return true;
    }
}
