package com.xiaosw.common.util

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.wifi.WifiManager
import android.os.Parcelable

import java.lang.ref.WeakReference
import java.util.ArrayList

/**
 * @ClassName [NetworkStatusHelper]
 * @Description 网络状态工具
 *
 * @Date 2018-02-02.
 * @Author xiaosw<xiaosw0802></xiaosw0802>@163.com>.
 */

object NetworkStatusHelper {

    private val TAG = "NetworkStatusHelper"
    private var sConnected: Boolean = false
    private val mListeners = ArrayList<WeakReference<OnNetStatusChangeListener>>()
    private var sNetStatusReceiver: NetStatusReceiver? = null

    /**
     * 当前释放成功连接网络
     * @return
     */
    /**
     * @hide
     * @param connected
     */
    var isConnected: Boolean
        get() = sConnected
        @Synchronized private set(connected) {
            if (sConnected != connected) {
                sConnected = connected
                val recycled: MutableList<WeakReference<OnNetStatusChangeListener>>? = null
                mListeners.filterNot { checkRecyclerdIfNeeded(recycled, it) }
                          .forEach { it.get()?.onNetStatusChange(sConnected) }
                recyclerListener(recycled)
            }
        }

    /**
     * 获取网络连接状态
     */
    fun initNetworkStatus() {
        getNetStatus()
        registerNetStatusRecevier()
    }

    @SuppressLint("MissingPermission")
    private fun getNetStatus() {
        val context = AndroidContext.get()
        val cm = context
                .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (cm != null) {
            val info = cm.allNetworkInfo
            if (info != null) {
                for (i in info.indices) {
                    if (info[i].state == NetworkInfo.State.CONNECTED) {
                        isConnected = true
                        break
                    }
                }
            }
        }
    }

    /**
     * 添加网络状态监听
     * @param listener
     */
    @Synchronized
    fun addNetStatusListener(listener: OnNetStatusChangeListener) {
        if (mListeners.size == 0) {
            mListeners.add(WeakReference(listener))
        } else {
            val recyclerd: MutableList<WeakReference<OnNetStatusChangeListener>>? = null
            var isAdded = false
            for (ref in mListeners) {
                checkRecyclerdIfNeeded(recyclerd, ref)
                if (null != ref && ref.get() === listener) {
                    isAdded = true
                }
            }
            recyclerListener(recyclerd)
            if (!isAdded) {
                mListeners.add(WeakReference(listener))
            }
        }
    }

    /**
     * 移除网络状态监听
     * @param listener
     * @return
     */
    @Synchronized
    fun removeNetStatusListener(listener: OnNetStatusChangeListener): Boolean {
        var isRemoved = false
        if (mListeners.size > 0) {
            val recycled: MutableList<WeakReference<OnNetStatusChangeListener>>? = null
            for (ref in mListeners) {
                if (!checkRecyclerdIfNeeded(recycled, ref) && listener === ref.get()) {
                    addToRecyclerd(recycled, ref)
                    isRemoved = true
                }
            }
            recyclerListener(recycled)
        }
        return isRemoved
    }

    /**
     * 检测是否有无效监听， 有则添加至回收队列
     * @param recyclerd
     * @param target
     * @return
     */
    private fun checkRecyclerdIfNeeded(recyclerd: MutableList<WeakReference<OnNetStatusChangeListener>>?,
                                       target: WeakReference<OnNetStatusChangeListener>): Boolean {
        if (null == target || target.get() == null) {
            addToRecyclerd(recyclerd, target)
            return true
        }
        return false
    }

    /**
     * 回收无效监听
     * @param recyclerd
     * @param target
     */
    private fun addToRecyclerd(recyclerd: MutableList<WeakReference<OnNetStatusChangeListener>>?,
                               target: WeakReference<OnNetStatusChangeListener>) {
        var recyclerd = recyclerd
        if (null == recyclerd) {
            recyclerd = ArrayList()
        }
        recyclerd.add(target)
    }

    private fun recyclerListener(recyclerd: List<WeakReference<OnNetStatusChangeListener>>?) {
        if (null != recyclerd && recyclerd.isNotEmpty()) {
            mListeners.removeAll(recyclerd)
        }
    }

    /**
     * 注册网络状态监听广播
     */
    private fun registerNetStatusRecevier() {
        unregsterNetStatusRecevier()
        sNetStatusReceiver = NetStatusReceiver()
        val filter = IntentFilter()
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION)
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION)
        AndroidContext.get().registerReceiver(sNetStatusReceiver, IntentFilter(filter))
    }

    /**
     * 注销网络状态监听广播
     */
    private fun unregsterNetStatusRecevier() {
        if (null != sNetStatusReceiver) {
            try {
                AndroidContext.get().unregisterReceiver(sNetStatusReceiver)
            } catch (e: Exception) {
                LogUtil.e(TAG, "unregsterNetStatusRecevier: ", e)
            }

            sNetStatusReceiver = null
        }
    }

    private class NetStatusReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (WifiManager.WIFI_STATE_CHANGED_ACTION == action) {
                when (intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, -1)) {
                    WifiManager.WIFI_STATE_DISABLED -> sConnected = false

                    WifiManager.WIFI_STATE_ENABLED -> getNetStatus()
                }
            } else if (WifiManager.NETWORK_STATE_CHANGED_ACTION == intent.action) {
                // 这个监听wifi的连接状态即是否连上了一个有效无线路由，当上边广播的状态是WifiManager
                // .WIFI_STATE_DISABLING，和WIFI_STATE_DISABLED的时候，根本不会接到这个广播。
                // 在上边广播接到广播是WifiManager.WIFI_STATE_ENABLED状态的同时也会接到这个广播，
                // 当然刚打开wifi肯定还没有连接到有效的无线
                val parcelableExtra = intent
                        .getParcelableExtra<Parcelable>(WifiManager.EXTRA_NETWORK_INFO)
                if (null != parcelableExtra) {
                    val networkInfo = parcelableExtra as NetworkInfo
                    val state = networkInfo.state
                    val isConnected = state == NetworkInfo.State.CONNECTED// 当然，这边可以更精确的确定状态
                    if (isConnected) {
                        sConnected = true
                    } else {
                        sConnected = false
                    }
                }
            } else if (ConnectivityManager.CONNECTIVITY_ACTION == intent.action) {
                // 这个监听网络连接的设置，包括wifi和移动数据的打开和关闭。.
                // 最好用的还是这个监听。wifi如果打开，关闭，以及连接上可用的连接都会接到监听。见log
                // 这个广播的最大弊端是比上边两个广播的反应要慢，如果只是要监听wifi，我觉得还是用上边两个配合比较合适
                getNetStatus()
            }
        }
    }

    /**
     * 监听网络状态
     */
    interface OnNetStatusChangeListener {

        /**
         * 网络状态变化
         * @param isConnected
         */
        fun onNetStatusChange(isConnected: Boolean)

    }

}
