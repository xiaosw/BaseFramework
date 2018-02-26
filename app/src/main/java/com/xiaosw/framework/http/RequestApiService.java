package com.xiaosw.framework.http;

import com.xiaosw.core.http.Result;
import com.xiaosw.framework.model.bean.AppH5UrlValue;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * @ClassName {@link RequestApiService}
 * @Description
 *
 * @Date 2018-02-26.
 * @Author xiaosw<xiaosw0802@163.com>.
 */

public interface RequestApiService {

    /**
     * 获取h5列表
     */
    @POST("system/appH5UrlValueList")
    Observable<Result<List<AppH5UrlValue>>> getAppH5UrlValueList(@Body RequestBody requestBody);

}
