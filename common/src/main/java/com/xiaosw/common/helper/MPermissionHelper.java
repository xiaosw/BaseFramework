package com.xiaosw.common.helper;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;

import com.xiaosw.common.helper.proxy.PermissionProxy;
import com.xiaosw.common.util.LogUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


/**
 * @ClassName {@link MPermissionHelper}
 * @Description
 *
 * @Date 2018-02-06.
 * @Author xiaosw<xiaosw0802@163.com>.
 */
public class MPermissionHelper {

    private static final String TAG = "MPermissionHelper";

    /**
     * auto generate suffix.
     */
    private static final String SUFFIX = "$$".concat(PermissionProxy.class.getSimpleName());

    public static void requestPermissions(Activity activity, int requestCode,
                                          String... permissions) {
        requestPermissionsImpl(activity, requestCode, permissions);
    }

    public static void requestPermissions(Fragment fragment, int requestCode,
                                          String... permissions) {
        requestPermissionsImpl(fragment, requestCode, permissions);
    }

    public static void requestPermissions(android.app.Fragment object,
                                          int requestCode, String... permissions) {
        requestPermissionsImpl(object, requestCode, permissions);
    }

    public static boolean shouldShowRequestPermissionRationale(Activity activity, String permission, int requestCode) {
        PermissionProxy proxy = findPermissionProxy(activity);
        if (!proxy.onNeedShowRationale(requestCode)) {
            return false;
        }
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                permission)) {
            proxy.onRationale(activity, requestCode, permission);
            return true;
        }
        return false;
    }

    @TargetApi(value = Build.VERSION_CODES.M)
    private static void requestPermissionsImpl(Object target, int requestCode, String... permissions) {
        if (!checkNeedDynamicAuth()) {
            onGrant(target, requestCode);
            return;
        }
        final Activity activity = getActivity(target);
        List<String> deniedPermissions = findDeniedPermissions(activity, permissions);

        if (deniedPermissions.size() > 0) {

            // check shouldShowRequestPermissionRationale
            List<String> shouldShowRequestPermissionRationales = new ArrayList<>();
            for (String deniedPermission : deniedPermissions) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity, deniedPermission)) {
                    shouldShowRequestPermissionRationales.add(deniedPermission);
                }
            }
            if (shouldShowRequestPermissionRationales.size() > 0) {
                findPermissionProxy(activity).onRationale(target, requestCode,
                        shouldShowRequestPermissionRationales.toArray(new String[shouldShowRequestPermissionRationales.size()]));
            } else {
                if (target instanceof Activity) {
                    ((Activity) target).requestPermissions(deniedPermissions.toArray(new String[deniedPermissions.size()]), requestCode);
                } else if (target instanceof Fragment) {
                    ((Fragment) target).requestPermissions(deniedPermissions.toArray(new String[deniedPermissions.size()]), requestCode);
                } else if (target instanceof android.app.Fragment) {
                    ((android.app.Fragment) target).requestPermissions(deniedPermissions.toArray(new String[deniedPermissions.size()]), requestCode);
                } else {
                    throw new IllegalArgumentException(target.getClass().getName() + " is not supported!");
                }
            }
        } else {
            onGrant(target, requestCode);
        }
    }

    private static PermissionProxy findPermissionProxy(Object activity) {
        try {
            Class clazz = activity.getClass();
            Class injectorClazz = Class.forName(clazz.getName() + SUFFIX);
            return (PermissionProxy) injectorClazz.newInstance();
        } catch (ClassNotFoundException e) {
            LogUtil.e(TAG, "findPermissionProxy: ", e);
        } catch (InstantiationException e) {
            LogUtil.e(TAG, "findPermissionProxy: ", e);
        } catch (IllegalAccessException e) {
            LogUtil.e(TAG, "findPermissionProxy: ", e);
        }
        throw new RuntimeException(String.format("can not find %s , something when compiler.", activity.getClass().getSimpleName() + SUFFIX));
    }


    private static void onGrant(Object target, int requestCode) {
        findPermissionProxy(target).onGrant(target, requestCode);
    }

    private static void onDenied(Object target, int requestCode) {
        findPermissionProxy(target).onDenied(target, requestCode);
    }

    public static void onRequestPermissionsResult(Activity target, int requestCode, String[] permissions,
                                                  int[] grantResults) {
        requestResultImpl(target, requestCode, permissions, grantResults);
    }

    public static void onRequestPermissionsResult(Fragment fragment, int requestCode, String[] permissions,
                                                  int[] grantResults) {
        requestResultImpl(fragment, requestCode, permissions, grantResults);
    }

    private static void requestResultImpl(Object obj, int requestCode, String[] permissions,
                                          int[] grantResults) {
        List<String> deniedPermissions = new ArrayList<>();
        for (int i = 0; i < grantResults.length; i++) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                deniedPermissions.add(permissions[i]);
            }
        }
        if (deniedPermissions.size() > 0) {
            onDenied(obj, requestCode);
        } else {
            onGrant(obj, requestCode);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // util
    ///////////////////////////////////////////////////////////////////////////
    public static boolean checkNeedDynamicAuth() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    @TargetApi(value = Build.VERSION_CODES.M)
    public static List<String> findDeniedPermissions(Activity activity, String... permissions) {
        List<String> denyPermissions = new ArrayList<>();
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                denyPermissions.add(permission);
            }
        }
        return denyPermissions;
    }

    public static List<Method> findAnnotationMethods(Class clazz, Class<? extends Annotation> clazz1) {
        List<Method> methods = new ArrayList<>();
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(clazz1)) {
                methods.add(method);
            }
        }
        return methods;
    }

    public static Activity getActivity(Object object) {
        if (object instanceof Fragment) {
            return ((Fragment) object).getActivity();
        } else if (object instanceof Activity) {
            return (Activity) object;
        } else if (object instanceof android.app.Fragment) {
            return ((android.app.Fragment) object).getActivity();
        }
        return null;
    }
}
