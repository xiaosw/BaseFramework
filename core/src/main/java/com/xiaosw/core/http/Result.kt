package com.xiaosw.core.http

import com.google.gson.annotations.SerializedName

import java.io.Serializable

/**
 * @ClassName [Result]
 * @Description
 *
 * @Date 2018-02-09.
 * @Author xiaosw<xiaosw0802></xiaosw0802>@163.com>.
 */

class Result<T> : Serializable {

    @SerializedName("code")
    val code: Int = 0
    @SerializedName("data")
    val data: T? = null
    @SerializedName("message")
    val message: String? = null
}
