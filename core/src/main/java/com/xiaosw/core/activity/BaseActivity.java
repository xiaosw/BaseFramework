package com.xiaosw.core.activity;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.xiaosw.common.helper.MPermissionHelper;

/**
 * @ClassName {@link BaseActivity}
 * @Description
 * @Date 2018-02-06.
 * @Author xiaosw<xiaosw0802@163.com>.
 */

public class BaseActivity extends AppCompatActivity {

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MPermissionHelper.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

}
