package com.xiaosw.framework.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.xiaosw.common.util.LogUtil;
import com.xiaosw.core.camera.BaseCamera;
import com.xiaosw.core.camera.MaskView;
import com.xiaosw.core.camera.SimpleCamera;
import com.xiaosw.framework.R;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class CameraActivity extends AppCompatActivity {

    private static final String TAG = "CameraActivity";

    private SurfaceView mSurfaceView;
    private MaskView mask_view;
    private BaseCamera mBaseCamera;
    private ImageView iv_result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        mSurfaceView = findViewById(R.id.camera_pre_view);
        mask_view = findViewById(R.id.mask_view);
        iv_result = findViewById(R.id.iv_result);
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

    public void takePicture(View view) {
        mBaseCamera.takePicture(new BaseCamera.OnTakePictureListener() {
            @Override
            public boolean onTakePictureSuccess(byte[] data) {
                mBaseCamera.restartPreview();
                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                // 矩阵
                Matrix matrix = new Matrix();
                matrix.setRotate(90);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                bitmap = mBaseCamera.cropMaskRectBitmap(bitmap, mask_view.getMaskRect());
                String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath().concat("/aa.jpeg");
                try {
                    BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(path));
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos);
                    bos.flush();//输出
                    bos.close();//关闭
                } catch (FileNotFoundException e) {
                    LogUtil.e(TAG, "onTakePictureSuccess: ", e);
                } catch (IOException e) {
                    LogUtil.e(TAG, "onTakePictureSuccess: ", e);
                }
                iv_result.setImageBitmap(bitmap);
                return true;
            }

            @Override
            public void onTakePictureFaile(int type, String description) {
                LogUtil.e(TAG, "onTakePictureFaile: " + description);
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
