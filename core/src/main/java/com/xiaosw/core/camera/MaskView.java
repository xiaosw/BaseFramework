package com.xiaosw.core.camera;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Region;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.xiaosw.common.util.LogUtil;

/**
 * @ClassName {@link MaskView}
 * @Description
 * @Date 2018-03-16.
 * @Author xiaosw<xiaosw0802@163.com>.
 */

public class MaskView extends View {

    private static final String TAG = "MaskView";

    private int mWidth, mHeight;
    private Rect mMaskRect;

    public MaskView(Context context) {
        super(context);
        init(context);
    }

    public MaskView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MaskView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MaskView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
        int width = Math.min(mWidth, mHeight);
        int centerX = mWidth / 2;
        int centerY = mHeight / 2;
        int maskWidth = (int) (width * 0.9f) >> 1;
        int maskHeight = (int) (width * 0.6f) >> 1;
        int l = centerX - maskWidth;
        int t = centerY - maskHeight;
        int r = centerX + maskWidth;
        int b = centerY + maskHeight;
        mMaskRect.set(l, t, r, b);
    }

    private void init(Context context) {
        mMaskRect = new Rect();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mWidth < 0 || mHeight < 0) {
            return;
        }
        canvas.save();
        canvas.clipRect(0, 0, mWidth, mHeight); // 画整个屏幕矩形
        canvas.clipRect(mMaskRect, Region.Op.DIFFERENCE); // 裁剪取景框的矩形
        canvas.drawColor(0x60000000);
        canvas.restore();
    }

    public Rect getMaskRect() {
        return mMaskRect;
    }

}