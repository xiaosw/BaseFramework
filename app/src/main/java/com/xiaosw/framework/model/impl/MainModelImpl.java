package com.xiaosw.framework.model.impl;

import com.xiaosw.core.http.Result;
import com.xiaosw.core.http.ServiceFactory;
import com.xiaosw.core.http.SimpleObserver;
import com.xiaosw.framework.config.AppConfig;
import com.xiaosw.framework.http.RequestApiService;
import com.xiaosw.framework.model.BaseModel;
import com.xiaosw.framework.model.IMainModel;
import com.xiaosw.framework.model.bean.AppH5UrlValue;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @ClassName {@link MainModelImpl}
 * @Description
 * @Date 2018-02-26.
 * @Author xiaosw<xiaosw0802@163.com>.
 */

public class MainModelImpl extends BaseModel implements IMainModel {

    @Override
    public void getH5Urls(SimpleObserver<Result<List<AppH5UrlValue>>> observer) {
        RequestApiService service = ServiceFactory.INSTANCE.createServiceFrom(AppConfig.URL_BASE, RequestApiService.class);
        service.getAppH5UrlValueList(buildEmptyBody())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .unsubscribeOn(Schedulers.io())
            .subscribe(observer);
    }
}
