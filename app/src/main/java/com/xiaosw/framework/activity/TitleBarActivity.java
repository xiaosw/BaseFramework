package com.xiaosw.framework.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaosw.core.activity.BaseActivity;
import com.xiaosw.core.presenter.BasePrecenter;
import com.xiaosw.framework.R;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * @ClassName {@link TitleBarActivity}
 * @Description
 *
 * @Date 2018-02-24.
 * @Author xiaosw<xiaosw0802@163.com>.
 */

public abstract class TitleBarActivity<T extends BasePrecenter> extends BaseActivity<T> {

    Toolbar mToolbar;
    ImageView mToolbarBack;
    TextView mToolbarTitle;
    FrameLayout mContentRoot;
    private Unbinder mBind;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_title_bar);
        mContentRoot = findViewById(R.id.content_panel);
        mToolbar = findViewById(R.id.toolbar);
        mToolbarBack = findViewById(R.id.toolbar_back);
        mToolbarTitle = findViewById(R.id.toolbar_title);
        setSupportActionBar(mToolbar);
    }

    private void removeAllViewsIfNeeded() {
        if (mContentRoot.getChildCount() > 0) {
            mContentRoot.removeAllViews();
        }
    }

    private void setContentViewComplete() {
        mBind = ButterKnife.bind(this);
        onViewCreated();
    }

    @Override
    public void setContentView(View view) {
        // super.setContentView(view);
        setContentView(view, null);
    }

    @Override
    public void setContentView(int layoutResID) {
//         super.setContentView(layoutResID);
        removeAllViewsIfNeeded();
        getLayoutInflater().from(this).inflate(layoutResID, mContentRoot);
        setContentViewComplete();
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        // super.setContentView(view, params);
        removeAllViewsIfNeeded();
        if (null == params) {
            mContentRoot.addView(view);
        } else {
            mContentRoot.addView(view, params);
        }
        setContentViewComplete();
    }

    @Override
    public void setTitle(CharSequence title) {
        // super.setTitle(title);
        mToolbarTitle.setText(title);
    }

    @Override
    public void setTitle(int titleId) {
        // super.setTitle(titleId);
        mToolbarTitle.setText(titleId);
    }

    @Override
    public void setTitleColor(int textColor) {
        // super.setTitleColor(textColor);
        mToolbarTitle.setTextColor(textColor);
    }

    @Override
    protected void onDestroy() {
        if (null != mBind) {
            mBind.unbind();
            mBind = null;
        }
        super.onDestroy();
    }

    ///////////////////////////////////////////////////////////////////////////
    // toolbar
    ///////////////////////////////////////////////////////////////////////////
    @OnClick(R.id.toolbar_back)
    void onBack(View view) {
        finish();
    }

    protected void hideBack() {
        if (mToolbarBack != null && mToolbarTitle.getVisibility() != View.INVISIBLE) {
            mToolbarBack.setVisibility(View.INVISIBLE);
        }
    }

    protected void showBack() {
        if (mToolbarBack != null && mToolbarTitle.getVisibility() != View.VISIBLE) {
            mToolbarBack.setVisibility(View.VISIBLE);
        }
    }

    protected void onViewCreated(){}
}
