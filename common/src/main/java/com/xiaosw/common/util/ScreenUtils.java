package com.xiaosw.common.util;

import android.content.Context;
import android.support.annotation.NonNull;

/**
 * @ClassName {@link ScreenUtils}
 * @Description 屏幕尺寸计算相关算法及其他较通用方法
 *
 * @Date 2018-02-26.
 * @Author xiaosw<xiaosw0802@163.com>.
 */

public class ScreenUtils {

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素) 
     * 如果dp<=0 如：ViewGroup.LayoutParams.WRAP_CONTENT，则原值返回
     */
    public static final float dp2px(@NonNull Context context, float dp) {
        return dp > (float) 0 ? dp * context.getResources().getDisplayMetrics().density : dp;
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp 
     * 如果px<=0 如：ViewGroup.LayoutParams.WRAP_CONTENT，则原值返回
     */
    public static final float px2dp(@NonNull Context context, float px) {
        return px > (float) 0 ? px / context.getResources().getDisplayMetrics().density : px;
    }

    /**
     * 根据手机的分辨率从 sp 的单位 转成为 px(像素) 
     * 如果sp<=0 如：ViewGroup.LayoutParams.WRAP_CONTENT，则原值返回
     */
    public static final int sp2px(@NonNull Context context, float sp) {
        return sp > (float) 0 ? (int) (0.5F + sp * context.getResources().getDisplayMetrics().scaledDensity) : (int) sp;
    }

}
