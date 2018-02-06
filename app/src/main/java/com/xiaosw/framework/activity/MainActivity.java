package com.xiaosw.framework.activity;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.xiaosw.core.activity.BaseActivity;
import com.xiaosw.common.helper.MPermissionHelper;
import com.xiaosw.common.util.LogUtil;
import com.xiaosw.framework.R;
import com.xiaosw.permission.annotation.PermissionDenied;
import com.xiaosw.permission.annotation.PermissionGrant;
import com.xiaosw.permission.annotation.ShowRequestPermissionRationale;

/**
 * @ClassName {@link MainActivity}
 * @Description
 *
 * @Date 2018-02-06.
 * @Author xiaosw<xiaosw0802@163.com>.
 */

public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";

    static {
        System.loadLibrary("xiaosw");
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((TextView) findViewById(R.id.sample_text)).setText(stringFromJNI());

        findViewById(R.id.sample_text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MPermissionHelper.requestPermissions(MainActivity.this,
                        100, Manifest.permission.CAMERA);
            }
        });
    }

    @PermissionGrant(requestCode = 100)
    public void getPermissionSuccess() {
        LogUtil.e(TAG, "getPermissionSuccess: ");
    }

    @PermissionDenied(requestCode = 100)
    public void getPermissionError() {
        LogUtil.e(TAG, "getPermissionError: ");
    }

    @ShowRequestPermissionRationale(requestCode = 100)
    public void getPermissionRationale() {
        LogUtil.e(TAG, "getPermissionRationale: ");
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    private native String stringFromJNI();

}
