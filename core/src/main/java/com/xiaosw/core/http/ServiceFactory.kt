package com.xiaosw.core.http

import com.xiaosw.common.util.LogUtil
import com.xiaosw.common.util.NetworkStatusHelper
import com.xiaosw.core.http.interceptor.EncryptInterceptor

import java.lang.ref.SoftReference
import java.util.ArrayList
import java.util.HashMap
import java.util.concurrent.TimeUnit

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/**
 * @ClassName [ServiceFactory]
 * @Description
 *
 * @Date 2018-02-02.
 * @Author xiaosw<xiaosw0802></xiaosw0802>@163.com>.
 */

object ServiceFactory {

    private val TAG = "ServiceFactory"

    /** Service Cache  */
    private val sServices = HashMap<String, ArrayList<SoftReference<ServiceEntry<*>>>>()
    /** need release object.  */
    private val sNeedRecycler = ArrayList<SoftReference<ServiceEntry<*>>>()

    fun <T> createServiceFrom(baseUrl: String, serviceClazz: Class<T>): T {
        // 无网提示
        if (!NetworkStatusHelper.isConnected) {
            // TODO: 2018/2/2

        }
        findFromCache(serviceClazz, baseUrl)?.apply {
            LogUtil.d(TAG, "createServiceFrom: find service 【${serviceClazz.name}】 from cache.")
            return targetService as T
        }
        //设置超时时间
        val httpClientBuilder = OkHttpClient.Builder()
                .addInterceptor(EncryptInterceptor())//加解密
                .connectTimeout(3000, TimeUnit.SECONDS)
                .readTimeout(3000, TimeUnit.SECONDS)

        val adapter = Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(httpClientBuilder.build())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create()) // 添加Rx适配器
                .addConverterFactory(GsonConverterFactory.create()) // 添加Gson转换器
                .build()

        val targetService = adapter.create(serviceClazz)
        // add to cache
        val service = ServiceEntry(serviceClazz, targetService)
        with(sServices[baseUrl]) {
            if (null != this) {
                add(SoftReference(service))
            } else{
                val cacheServiceEntries = ArrayList<SoftReference<ServiceEntry<*>>>()
                cacheServiceEntries.add(SoftReference(service))
                sServices.put(baseUrl, cacheServiceEntries)
            }
        }
        return targetService
    }

    /**
     * find chace service
     * @param serviceClazz
     * @param baseUrl
     * @return
     */
    @Synchronized
    private fun findFromCache(serviceClazz: Class<*>, baseUrl: String): ServiceEntry<*>? {
        var target: ServiceEntry<*>? = null
        val cacheServices = sServices[baseUrl]
        cacheServices?.filter {
            val serviceEntry: ServiceEntry<*>? = it.get()
            if (serviceEntry?.serverClazz == null || serviceEntry.targetService == null) {
                sNeedRecycler.add(it)
                return@filter false
            }
            return@filter true
        }?.forEach {
            val serviceEntry = it.get()
            if (serviceEntry?.serverClazz == serviceClazz) {
                if (null != serviceEntry.targetService) {
                    return serviceEntry
                }
            }
        }
        return target
    }

    /**
     * 释放已被回收的对象
     * @param serviceEntries
     */
    private fun recyclerIfNeeded(serviceEntries: ArrayList<SoftReference<ServiceEntry<*>>>) {
        sNeedRecycler.apply {
            if (size > 0) {
                serviceEntries.removeAll(this)
                LogUtil.d(TAG, "recyclerIfNeeded: recycer size = $size")
                clear()
            }
        }
    }

    /**
     * Service缓存实体
     */
    private class ServiceEntry<T>(val serverClazz: Class<*>, val targetService: T)

}
