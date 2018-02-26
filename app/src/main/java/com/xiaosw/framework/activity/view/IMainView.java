package com.xiaosw.framework.activity.view;

import com.xiaosw.core.activity.view.IView;
import com.xiaosw.framework.model.bean.AppH5UrlValue;

import java.util.List;

/**
 * @ClassName {@link IMainView}
 * @Description
 * @Date 2018-02-26.
 * @Author xiaosw<xiaosw0802@163.com>.
 */

public interface IMainView extends IView {

    void handleH5Urls(List<AppH5UrlValue> h5);

}
