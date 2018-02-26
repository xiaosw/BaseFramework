package com.xiaosw.framework.model;

import com.xiaosw.core.http.Result;
import com.xiaosw.core.http.SimpleObserver;
import com.xiaosw.core.model.IModel;
import com.xiaosw.framework.model.bean.AppH5UrlValue;

import java.util.List;

/**
 * @ClassName {@link IMainModel}
 * @Description
 *
 * @Date 2018-02-26.
 * @Author xiaosw<xiaosw0802@163.com>.
 */

public interface IMainModel extends IModel {

    void getH5Urls(SimpleObserver<Result<List<AppH5UrlValue>>> observer);

}
