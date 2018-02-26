package com.xiaosw.core.http.interceptor

import com.xiaosw.common.util.LogUtil
import com.xiaosw.core.http.HttpConfig
import com.xiaosw.core.http.encrypt.Base64Utils
import com.xiaosw.core.http.encrypt.RSAUtils

import java.io.IOException
import java.nio.charset.Charset

import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.Response
import okio.Buffer

/**
 * @ClassName [EncryptInterceptor]
 * @Description 加解密
 *
 * @Date 2018-02-02.
 * @Author xiaosw<xiaosw0802></xiaosw0802>@163.com>.
 */

class EncryptInterceptor : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        val url = request.url().toString()
        var isEncrypt = true
        if (EncryptUrlWhiteList.isWhiteList(url)) {
            isEncrypt = false
        }
        val method = request.method()
        LogUtil.d(TAG, "intercept: request address = $url, isEncrypt = $isEncrypt, method = $method")
        var charset: Charset?
        if (isEncrypt) {
            val requestBody = request.body()
            val buffer = Buffer()
            requestBody!!.writeTo(buffer)
            charset = Charset.forName(HttpConfig.UTF_8)
            val mediaType = requestBody.contentType()
            if (null != mediaType) {
                charset = mediaType.charset(charset)
            }
            val requestParams = buffer.readString(charset!!)
            // 重置请求
            request = request.newBuilder()
                    .method(method, RequestBody.create(MediaType.parse("application/json; charset=utf-8"),
                            encrypt(requestParams)))
                    .build()
        }

        // 拦截返回的 response
        val response = chain.proceed(request)
        val responseBody = response.body()
        if (isEncrypt) {
            // 解密
            val source = responseBody!!.source()
            source.request(java.lang.Long.MAX_VALUE) // Buffer the entire body.
            val buffer = source.buffer()
            charset = Charset.forName(HttpConfig.UTF_8)
            val mediaType = responseBody.contentType()
            if (null != mediaType) {
                charset = mediaType.charset(charset)
            }
            val result = decrypt(buffer.readString(charset!!))
            source.buffer().write(result.toByteArray())
            LogUtil.d(TAG, "intercept: decrypt data = ${buffer.clone().readString(charset)}")
        }
        return response
    }

    /**
     * 加密
     * @param content
     * @return
     */
    @Synchronized
    private fun encrypt(content: String): String {
        var content = content
        try {
            LogUtil.d(TAG, "encrypt: content = $content")
            // 加密算法实现
            content = Base64Utils.encode(RSAUtils.encryptByPublicKey(content.toByteArray(Charsets.UTF_8),
                    RSAUtils.getPublicKey()))
        } catch (e: Exception) {
            LogUtil.e(TAG, "encrypt: ", e)
        }

        LogUtil.d(TAG, "intercept: request encrypt params = $content")
        return content
    }

    /**
     * 解密
     * @param content
     * @return
     */
    @Synchronized
    private fun decrypt(content: String): String {
        try {
            LogUtil.d(TAG, "decrypt: content = $content")
            // 解密算法实现
            return String(RSAUtils.decryptByPrivateKey(Base64Utils.decode(content), RSAUtils.getPrivateKey()))
        } catch (e: Exception) {
            LogUtil.e(TAG, "decrypt: ", e)
        }

        return content
    }

    companion object {
        private val TAG = "EncryptInterceptor"
    }

}
