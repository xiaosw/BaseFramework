package com.xiaosw.framework.presenter;

import com.xiaosw.core.http.Result;
import com.xiaosw.core.http.SimpleObserver;
import com.xiaosw.core.presenter.BasePrecenter;
import com.xiaosw.framework.activity.view.IMainView;
import com.xiaosw.framework.model.IMainModel;
import com.xiaosw.framework.model.bean.AppH5UrlValue;
import com.xiaosw.framework.model.impl.MainModelImpl;

import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @ClassName {@link MainPresenter}
 * @Description
 *
 * @Date 2018-02-26.
 * @Author xiaosw<xiaosw0802@163.com>.
 */

public class MainPresenter extends BasePrecenter<IMainView, IMainModel> {

    @Nullable
    @Override
    protected IMainModel bindModel() {
        return new MainModelImpl();
    }

    public void getH5Urls() {
        if (isBindModel()) {
            getModel().getH5Urls(new SimpleObserver<Result<List<AppH5UrlValue>>>(this) {
                @Override
                public void doNext(Result<List<AppH5UrlValue>> result) {
                    List<AppH5UrlValue> data = result.getData();
                    if (data != null && data.size() > 0 && isBindView()) {
                        getMView().handleH5Urls(data);
                    }
                }
            });
        }
    }

}
