package com.xiaosw.framework.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.FeatureInfo;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.xiaosw.core.camera.BaseCamera;
import com.xiaosw.core.camera.SimpleCamera;
import com.xiaosw.framework.R;


public class CameraActivity extends AppCompatActivity {

    private SurfaceView mSurfaceView;
    private BaseCamera mBaseCamera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        mSurfaceView = findViewById(R.id.camera_pre_view);
        mBaseCamera = new SimpleCamera(this);
        mBaseCamera.setSurfaceHolder(mSurfaceView.getHolder());
        mSurfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mBaseCamera.focusOnTouch((int) event.getX(), (int) event.getY());
                return false;
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (null != mBaseCamera) {
            mBaseCamera.release();
        }
        super.onDestroy();
    }
}
