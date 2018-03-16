package com.xiaosw.common.util

import android.content.Context
import android.content.res.Configuration
import android.os.Build

/**
 * @ClassName {@link DeviceUtils}
 * @Description
 *
 * @Date 2018-03-14.
 * @Author xiaosw<xiaosw0802@163.com>.
 */

/** >=2.2  */
fun hasFroyo(): Boolean {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO
}

/** >=2.3  */
fun hasGingerbread(): Boolean {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD
}

/** >=3.0 LEVEL:11  */
fun hasHoneycomb(): Boolean {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB
}

/** >=3.1  */
fun hasHoneycombMR1(): Boolean {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1
}

/** >=4.0 14  */
fun hasICS(): Boolean {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH
}

/**
 * >= 4.1 16
 *
 * @return
 */
fun hasJellyBean(): Boolean {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
}

/** >= 4.2 17  */
fun hasJellyBeanMr1(): Boolean {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1
}

/** >= 4.3 18  */
fun hasJellyBeanMr2(): Boolean {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2
}

/** >=4.4 19  */
fun hasKitkat(): Boolean {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
}

fun getSDKVersionInt(): Int {
    return Build.VERSION.SDK_INT
}

fun getSDKVersion(): String {
    return Build.VERSION.SDK
}

/**
 * 获得设备的固件版本号
 */
fun getReleaseVersion(): String {
    return StringUtils.makeSafe(Build.VERSION.RELEASE)
}

/** 检测是否是中兴机器  */
fun isZte(): Boolean {
    return getDeviceModel().toLowerCase().indexOf("zte") != -1
}

/** 判断是否是三星的手机  */
fun isSamsung(): Boolean {
    return getManufacturer().toLowerCase().indexOf("samsung") != -1
}

/** 检测是否HTC手机  */
fun isHTC(): Boolean {
    return getManufacturer().toLowerCase().indexOf("htc") != -1
}

/**
 * 检测当前设备是否是特定的设备
 *
 * @param devices
 * @return
 */
fun isDevice(vararg devices: String): Boolean {
    val model = getDeviceModel()
    if (devices != null && model != null) {
        for (device in devices) {
            if (model!!.indexOf(device) != -1) {
                return true
            }
        }
    }
    return false
}

/**
 * 获得设备型号
 *
 * @return
 */
fun getDeviceModel(): String {
    return StringUtils.trim(Build.MODEL)
}

/** 获取厂商信息  */
fun getManufacturer(): String {
    return StringUtils.trim(Build.MANUFACTURER)
}

/**
 * 判断是否是平板电脑
 *
 * @param context
 * @return
 */
fun isTablet(context: Context): Boolean {
    return context.getResources().getConfiguration().screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK >= Configuration.SCREENLAYOUT_SIZE_LARGE
}

/**
 * 检测是否是平板电脑
 *
 * @param context
 * @return
 */
fun isHoneycombTablet(context: Context): Boolean {
    return hasHoneycomb() && isTablet(context)
}