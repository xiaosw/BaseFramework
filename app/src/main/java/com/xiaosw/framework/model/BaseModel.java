package com.xiaosw.framework.model;

import com.xiaosw.common.util.GsonUtils;
import com.xiaosw.core.model.IModel;
import com.xiaosw.framework.model.bean.BaseParams;

import okhttp3.RequestBody;

/**
 * @ClassName {@link BaseModel}
 * @Description
 * @Date 2018-02-26.
 * @Author xiaosw<xiaosw0802@163.com>.
 */

public class BaseModel implements IModel {

    @Override
    public void cancel() {

    }

    protected RequestBody buildEmptyBody() {
        return buildBody("{}");
    }

    protected RequestBody buildBody(BaseParams params) {
        return buildBody(GsonUtils.INSTANCE.toJson(params));
    }

    protected RequestBody buildBody(String data) {
        return RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),
                data);
    }

}
