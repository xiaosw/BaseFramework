package com.xiaosw.core.activity.fragment;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import com.xiaosw.common.helper.MPermissionHelper;

/**
 * @ClassName {@link BaseFragment}
 * @Description
 * @Date 2018-02-07.
 * @Author xiaosw<xiaosw0802@163.com>.
 */

public abstract class BaseFragment extends Fragment {

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MPermissionHelper.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }
}
