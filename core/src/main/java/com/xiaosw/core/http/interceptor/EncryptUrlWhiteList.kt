package com.xiaosw.core.http.interceptor

import java.util.HashMap

/**
 * @ClassName [EncryptUrlWhiteList]
 * @Description 加解密白名单。 默认做加解密操作，用户可配置指定url不做加解密操作。
 *
 * @Date 2018-02-02.
 * @Author xiaosw<xiaosw0802></xiaosw0802>@163.com>.
 */

object EncryptUrlWhiteList {

    private val mEncryptUrlWhiteList by lazy {
        HashMap<String, Boolean>()
    }

    fun add(url: String, isWhiteList: Boolean): Boolean? {
        return mEncryptUrlWhiteList.put(url, isWhiteList)
    }

    fun isWhiteList(url: String): Boolean {
        return mEncryptUrlWhiteList[url] ?: return false
    }

}
