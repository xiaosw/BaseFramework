package com.xiaosw.core;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.xiaosw.common.util.AndroidContext;

/**
 * @ClassName {@link BaseApplication}
 * @Description
 *  1、init global context. init to {@link AndroidContext#init(Application)}；
 *  2、set use muliti dex if needed {@link #useMultiDex()} ;
 *
 * @Date 2018-02-06.
 * @Author xiaosw<xiaosw0802@163.com>.
 */

public abstract class BaseApplication extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        if (useMultiDex()) {
            MultiDex.install(this);
        }
        AndroidContext.INSTANCE.init(this);
    }

    /**
     * whether use multi dex.
     * @return true：use multi dex，false：use single dex. default is false
     */
    protected abstract boolean useMultiDex();

}
