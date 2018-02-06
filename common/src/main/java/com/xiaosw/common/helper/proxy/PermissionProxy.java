package com.xiaosw.common.helper.proxy;

/**
 * @ClassName {@link PermissionProxy}
 * @Description
 *
 * @Date 2018-02-06.
 * @Author xiaosw<xiaosw0802@163.com>.
 */
public interface PermissionProxy<T> {
    void onGrant(T source, int requestCode);

    void onDenied(T source, int requestCode);

    void onRationale(T source, int requestCode, String... permissions);

    boolean onNeedShowRationale(int requestCode);
}
