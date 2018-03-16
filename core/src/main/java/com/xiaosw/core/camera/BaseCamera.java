package com.xiaosw.core.camera;


import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Build;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.WindowManager;

import com.xiaosw.common.util.DeviceUtilsKt;
import com.xiaosw.common.util.LogUtil;
import com.xiaosw.common.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @ClassName {@link BaseCamera}
 * @Description 相机基类
 *
 * @Date 2018-03-16.
 * @Author xiaosw<xiaosw0802@163.com>.
 */

public abstract class BaseCamera implements SurfaceHolder.Callback {

    private static final String TAG = "BaseCamera";

    /**
     * 最大帧率
     */
    protected static int MAX_FRAME_RATE = 20;
    /**
     * 最小帧率
     */
    protected static int MIN_FRAME_RATE = 8;

    /**
     * 摄像头
     */
    private Camera mCamera;

    /**
     * 当前已开启摄像头id {@link Camera.CameraInfo#CAMERA_FACING_FRONT}
     * or {@link Camera.CameraInfo#CAMERA_FACING_BACK}
     */
    private int mCurrentCameraId = -1;

    protected Context mContext;
    private SurfaceHolder mSurfaceHolder;
    private boolean mStartedPreview;

    private int mWidth;
    private int mHeight;
    private WindowManager mWindowManager;

    public BaseCamera(Context context) {
        mContext = context;
        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
    }

    public void setSurfaceHolder(SurfaceHolder holder) {
        if (null != holder) {
            holder.addCallback(this);
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        this.mSurfaceHolder = holder;
        startPreview();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        this.mSurfaceHolder = holder;
        mWidth = width;
        mHeight = height;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mSurfaceHolder = null;
    }

    public void startPreview() {
        if (!mStartedPreview) {
            open();
            if (null != mCamera) {
                Camera.Parameters parameters = mCamera.getParameters();
                List<Camera.Size> supportedPictureSizes = parameters.getSupportedPictureSizes();
                Camera.Size size = null;
                for (Camera.Size supportedPictureSize : supportedPictureSizes) {
                    if (null == size) {
                        size = supportedPictureSize;
                    } else if (size.width < supportedPictureSize.width && size.height < supportedPictureSize.height) {
                        size = supportedPictureSize;
                    }
                }
                if (null != size) {
                    parameters.setPictureSize(size.width, size.height);
                    mCamera.setParameters(parameters);
                }
                adjustCameraRotation();
                setAutoFocus();
                try {
                    mCamera.setPreviewDisplay(mSurfaceHolder);
                    mCamera.startPreview();
                    mStartedPreview = true;
                } catch (IOException e) {
                    LogUtil.e(TAG, "startPreview: ", e);
                }
            }
        }
    }

    public Camera open() {
        return open(Camera.CameraInfo.CAMERA_FACING_BACK);
    }

    public Camera open(int cameraId) {
        if (checkIsSupport(cameraId)) {
            if (mCamera != null) {
                if (mCurrentCameraId == cameraId) {
                    return mCamera;
                } else {
                    release();
                }
            }
            mCurrentCameraId = cameraId;
            mCamera = Camera.open(cameraId);
        } else {
            LogUtil.e(TAG, String.format("openCamera: cameraId = %d is not support!", cameraId));
        }
        return mCamera;
    }

    protected void adjustCameraRotation() {
        if (mCamera != null) {
            int degrees = 0;
            switch (mWindowManager.getDefaultDisplay().getRotation()) {
                case Surface.ROTATION_0:
                    degrees = 90;
                    break;

                case Surface.ROTATION_90:
                    degrees = 0;
                    break;

                case Surface.ROTATION_180:
                    degrees = 90;
                    break;

                case Surface.ROTATION_270:
                    degrees = 180;
                    break;
            }
            mCamera.setDisplayOrientation(degrees);
        }
    }

    /**
     * release {@link Camera}
     */
    public void release() {
        if (null == mCamera) {
            mCamera.stopPreview();
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
            mStartedPreview = false;
        }
    }

    /**
     * 检测是否支持该摄像头
     * @param cameraId
     * @return
     */
    protected  boolean checkIsSupport(int cameraId) {
        if (cameraId == Camera.CameraInfo.CAMERA_FACING_BACK
                || cameraId == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            if (cameraId == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                return isSupportFrontCamera();
            }
            return true;
        }
        return false;
    }

    /**
     * checke is support font camera.
     */
    @SuppressLint("NewApi")
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    protected boolean isSupportFrontCamera() {
        if (!DeviceUtilsKt.hasGingerbread()) {
            return false;
        }
        int numberOfCameras = Camera.getNumberOfCameras();
        if (2 == numberOfCameras) {
            return true;
        }
        return false;
    }

    /**
     * 连续自动对焦
     */
    private String getAutoFocusMode(Camera.Parameters parameters) {
        if (parameters != null) {
            //持续对焦是指当场景发生变化时，相机会主动去调节焦距来达到被拍摄的物体始终是清晰的状态。
            List<String> focusModes = parameters.getSupportedFocusModes();
            if ((Build.MODEL.startsWith("GT-I950") || Build.MODEL.endsWith("SCH-I959") || Build.MODEL.endsWith("MEIZU MX3")) && isSupported(focusModes, "continuous-picture")) {
                return "continuous-picture";
            } else if (isSupported(focusModes, "continuous-video")) {
                return "continuous-video";
            } else if (isSupported(focusModes, "auto")) {
                return "auto";
            }
        }
        return null;
    }

    /**
     * 检测是否支持指定特性
     */
    private boolean isSupported(List<String> list, String key) {
        return list != null && list.contains(key);
    }

    public void setAutoFocus() {
        if (mCamera != null) {
            Camera.Parameters parameters = mCamera.getParameters();
            if (null != parameters) {
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO); // 设置聚焦模式
                mCamera.setParameters(parameters);
            }
        }
    }

    public void cancelAutoFocus() {
        if (mCamera != null) {
            mCamera.cancelAutoFocus();
        }
    }

    public void focusOnTouch(int x, int y) {
        Rect rect = new Rect(x - 100, y - 100, x + 100, y + 100);
        int left = rect.left * 2000 / mWidth - 1000;
        int top = rect.top * 2000 / mHeight - 1000;
        int right = rect.right * 2000 / mWidth - 1000;
        int bottom = rect.bottom * 2000 / mHeight - 1000;
        // 如果超出了(-1000,1000)到(1000, 1000)的范围，则会导致相机崩溃
        left = left < -1000 ? -1000 : left;
        top = top < -1000 ? -1000 : top;
        right = right > 1000 ? 1000 : right;
        bottom = bottom > 1000 ? 1000 : bottom;
        focusOnRect(new Rect(left, top, right, bottom));
    }

    public void focusOnRect(Rect rect) {
        if (mCamera != null) {
            Camera.Parameters parameters = mCamera.getParameters(); // 先获取当前相机的参数配置对象
            if (parameters == null) {
                return;
            }
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO); // 设置聚焦模式
            LogUtil.e(TAG, "parameters.getMaxNumFocusAreas() : " + parameters.getMaxNumFocusAreas());
            if (parameters.getMaxNumFocusAreas() > 0) {
                List<Camera.Area> focusAreas = new ArrayList<Camera.Area>();
                focusAreas.add(new Camera.Area(rect, 1000));
                parameters.setFocusAreas(focusAreas);
            }
            mCamera.cancelAutoFocus(); // 先要取消掉进程中所有的聚焦功能
            mCamera.setParameters(parameters); // 一定要记得把相应参数设置给相机
            mCamera.autoFocus(new Camera.AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean success, Camera camera) {
                    LogUtil.e(TAG, "onAutoFocus: success = " + success);
                }
            });
        }
    }
}
