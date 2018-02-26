package com.xiaosw.core.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.xiaosw.common.helper.MPermissionHelper;
import com.xiaosw.common.util.LogUtil;
import com.xiaosw.core.activity.view.IView;
import com.xiaosw.core.presenter.BasePrecenter;

import org.jetbrains.annotations.NotNull;

/**
 * @ClassName {@link BaseActivity}
 * @Description
 *
 * @Date 2018-02-06.
 * @Author xiaosw<xiaosw0802@163.com>.
 */

public abstract class BaseActivity<P extends BasePrecenter> extends AppCompatActivity implements
        IView {

    private static final String TAG = "BaseActivity";

    private P mPresenter;
    protected Activity mActivity;
    protected Context mAppContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;
        mAppContext = getApplicationContext();
        onHandleIntent(getIntent());
        mPresenter = buildPrecenter();
        if (null != mPresenter) {
            mPresenter.bindView(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MPermissionHelper.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    /**
     * @deprecated use {@link #onHandleIntent(Intent)}
     * @param intent
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        dispatchIntent(intent);
    }

    private void dispatchIntent(Intent intent) {
        if (null != intent) {
            onHandleIntent(intent);
        } else {
            LogUtil.w(TAG, "onHandleIntent: intent is null!");
        }
    }

    protected void onHandleIntent(Intent intent) {}

    ///////////////////////////////////////////////////////////////////////////
    // abstract method
    ///////////////////////////////////////////////////////////////////////////
    protected abstract P buildPrecenter();

    ///////////////////////////////////////////////////////////////////////////
    // new method
    ///////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////
    // add new api
    ///////////////////////////////////////////////////////////////////////////
    public boolean isBindPrecenter() {
        return mPresenter != null;
    }

    protected P getPresenter() {
        return mPresenter;
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void dissmissLoading() {

    }

    @NotNull
    @Override
    public Context getContext() {
        return mActivity;
    }

    @NotNull
    @Override
    public Context getAppContext() {
        return mAppContext;
    }
}
