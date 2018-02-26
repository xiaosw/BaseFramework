package com.xiaosw.framework.activity;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.xiaosw.common.helper.MPermissionHelper;
import com.xiaosw.common.util.LogUtil;
import com.xiaosw.framework.R;
import com.xiaosw.framework.activity.view.IMainView;
import com.xiaosw.framework.model.bean.AppH5UrlValue;
import com.xiaosw.framework.presenter.MainPresenter;
import com.xiaosw.permission.annotation.PermissionDenied;
import com.xiaosw.permission.annotation.PermissionGrant;
import com.xiaosw.permission.annotation.ShowRequestPermissionRationale;

import java.util.List;

import butterknife.BindView;

/**
 * @ClassName {@link MainActivity}
 * @Description
 *
 * @Date 2018-02-06.
 * @Author xiaosw<xiaosw0802@163.com>.
 */

public class MainActivity extends TitleBarActivity<MainPresenter> implements IMainView {

    private static final String TAG = "MainActivity";

    static {
        System.loadLibrary("xiaosw");
    }

    @BindView(R.id.sample_text) TextView sample_text;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (isBindPrecenter()) {
            getPresenter().getH5Urls();
        }
    }

    @Override
    protected MainPresenter buildPrecenter() {
        return new MainPresenter();
    }

    @Override
    protected void onViewCreated() {
        hideBack();
        setTitle("Home");
        sample_text.setText(stringFromJNI());
        sample_text.setOnClickListener(new View.OnClickListener() {
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

    @Override
    public void handleH5Urls(List<AppH5UrlValue> h5s) {
        for (AppH5UrlValue h5 : h5s) {
            LogUtil.e(TAG, "handleH5Urls: " + h5);
        }
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    private native String stringFromJNI();

}
