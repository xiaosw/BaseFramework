package com.xiaosw.core.widget.textpath;

import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import com.xiaosw.common.util.LogUtil;
import com.xiaosw.core.R;

import java.util.ArrayList;

/**
 * @ClassName {@link TextPathView}
 * @Description
 *
 * @Date 2018-03-29.
 * @Author xiaosw<xiaosw0802@163.com>.
 */

public class TextPathView extends View {
    
    private static final String TAG = "xiaosw-TextPathView";

    final private Interpolator mInterpolator = new LinearInterpolator();
    /** 内容宽度 */
    protected int mContentWidth;
    /** 内容高度 */
    protected int mContentHeight;
    /** 绘制文字画笔 */
    private Paint mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    /** 文字全path */
    private Path mTextPath = new Path();
    /** 用于接收测量path */
    private Path mOutPath = new Path();
    /** 文字百分比path */
    private Path mPercentTextPath = new Path();
    /** 测量path. */
    private PathMeasure mPathMeasure = new PathMeasure();

    private float mMax = 100;
    private float mProgress = 0;
    /** 更新动画 */
    private ValueAnimator mValueAnimator;
    private int mAnimDuration;
    private int mGravity;
    private int mRepeatMode = ValueAnimator.RESTART;
    private int mRepeatCount = 0;

    ///////////////////////////////////////////////////////////////////////////
    // attrs
    ///////////////////////////////////////////////////////////////////////////
    private volatile boolean isNeededHandleNewLine;
    private String mText;
    private int mTextColor = Color.BLACK;
    private boolean isAutoDraw;

    public TextPathView(Context context) {
        super(context);
        initialize(context, null);
    }

    public TextPathView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initialize(context, attrs);
    }

    public TextPathView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context , attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TextPathView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initialize(context , attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        handleNewLineIfNeeded(width, height);
        if (widthMeasureSpec != ViewGroup.LayoutParams.WRAP_CONTENT) {
            mContentHeight = width;
        } else {
            mContentWidth = Math.min(width, mContentWidth);
        }
        if (heightMeasureSpec != ViewGroup.LayoutParams.WRAP_CONTENT) {
            mContentHeight = height;
        } else {
            mContentHeight = Math.min(height, mContentHeight);
        }
        setMeasuredDimension(mContentWidth, mContentHeight);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (isAutoDraw) {
            startAnim();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        cancelAnimIfNeeded();
        super.onDetachedFromWindow();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(mPercentTextPath, mTextPaint);
    }

    /**
     * 初始化
     * @param context
     * @param attrs
     */
    private void initialize(Context context, AttributeSet attrs) {
        parseDefAttrs(context, attrs);
        initPaint();
        init();
    }

    /**
     * 解析自定义属性
     * @param context
     * @param attrs
     */
    private void parseDefAttrs(Context context, AttributeSet attrs) {
        if (null != attrs) {
            // parse
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TextPathView);
            mText = ta.getString(R.styleable.TextPathView_android_text);
            isAutoDraw = ta.getBoolean(R.styleable.TextPathView_autoDraw, true);
            mGravity = ta.getInt(R.styleable.TextPathView_android_gravity, Gravity.LEFT);
            setTextColor(ta.getColor(R.styleable.TextPathView_android_textColor, mTextColor));
            setRawTextSize(ta.getDimensionPixelSize(R.styleable.TextPathView_android_textSize, 32), false);
            setText(mText);
            ta.recycle();
        }
    }

    /**
     * 初始化画笔
     */
    private void initPaint() {
        mTextPaint.setStrokeWidth(3);
        mTextPaint.setStyle(Paint.Style.STROKE);
    }

    private void initAnim(float start, float end, int repeatMode, int repeatCount) {
        cancelAnimIfNeeded();
        mValueAnimator = ValueAnimator.ofFloat(start, end);
        mValueAnimator.setDuration(mAnimDuration);
        mValueAnimator.setInterpolator(mInterpolator);
        mValueAnimator.setRepeatMode(repeatMode);
        mValueAnimator.setRepeatCount(repeatCount);
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                setProgress(mMax * value);
            }
        });
        mValueAnimator.start();
    }

    private void cancelAnimIfNeeded() {
        if (mValueAnimator != null && mValueAnimator.isRunning()) {
            mValueAnimator.cancel();
        }
        mValueAnimator = null;
    }

    /**
     * 计算最大值
     */
    private void calculatorMax() {
        mMax = 0f;
        if (!TextUtils.isEmpty(mText)) {
            mTextPaint.getTextPath(mText, 0, mText.length(), 0, 0, mTextPath);
            mPathMeasure.setPath(mTextPath, false);
            mMax = mPathMeasure.getLength();
            while (mPathMeasure.nextContour()) {
                mMax += mPathMeasure.getLength();
            }
        }
        mAnimDuration = (int) mMax;
    }

    /**
     * 计算换行
     * @param width
     */
    protected void handleNewLineIfNeeded(int width, int height) {
        if (!isNeededHandleNewLine) {
            LogUtil.w(TAG, "handleNewLineIfNeeded: already handle new line.");
            return;
        }
        mTextPath.reset();
        String text = mText;
        float textWidth = mTextPaint.measureText(text);
        final Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        final int singleLineHeight = (int) (fontMetrics.bottom - fontMetrics.top);
        mContentHeight = singleLineHeight;
        ArrayList<String> texts = new ArrayList<>();
        int endIndex = 0;
        int totalWidth;
        if (textWidth <= width) {
            // single line
            totalWidth = (int) textWidth;
            endIndex = text.length();
            texts.add(text);
        } else {
            mContentWidth = width;
            float[] widths = new float[text.length()];
            mTextPaint.getTextWidths(text, widths);
            final int xOffset = getPaddingLeft() + getPaddingRight();
            totalWidth = xOffset;
            for (float w : widths) {
                totalWidth += w;
                if (totalWidth > width) {
                    texts.add(text.substring(0, endIndex));
                    mContentHeight += singleLineHeight;
                    text = text.substring(endIndex);
                    totalWidth = xOffset;
                    endIndex = 0;
                }
                endIndex ++;
            }
        }
        final int size = texts.size();
        final float verticalTransition = (height - getPaddingTop() - getPaddingBottom() - mContentHeight) / 2;
        for (int i = 0; i < size; i++) {
            calculatorTextPath(text.substring(0, endIndex),
                    width - totalWidth,
                    singleLineHeight * i,
                    verticalTransition,
                    fontMetrics);
        }
        mContentWidth += getPaddingTop() + getPaddingBottom();
        mContentHeight += getPaddingLeft() + getPaddingRight();
        if (isAutoDraw) {
            setProgress(0);
        } else {
            setProgress(mMax);
        }
        isNeededHandleNewLine = false;
        LogUtil.d(TAG, String.format("handleNewLineIfNeeded: mMax = %f", mMax));
    }

    /**
     * 计算文字路径
     * @return
     */
    private void calculatorTextPath(String text, int xOffset, float yOffset, float verticalTransition, Paint.FontMetrics fontMetrics) {
        if (!TextUtils.isEmpty(text)) {
            int x = getPaddingLeft();
            float y = (fontMetrics.bottom - fontMetrics.top) / 2 + fontMetrics.descent + yOffset + getPaddingTop();
            if (mGravity == Gravity.CENTER_HORIZONTAL) {
                x += xOffset / 2;
            } else if (mGravity == Gravity.CENTER) {
                x += xOffset / 2;
                y += verticalTransition;
            } else if (mGravity == Gravity.CENTER_VERTICAL) {
                y += verticalTransition;
            }

            mTextPaint.getTextPath(text, 0, text.length(), x, y, mOutPath);
            mTextPath.addPath(mOutPath);
        }
    }

    protected void init() {
    }

    ///////////////////////////////////////////////////////////////////////////
    // public api
    ///////////////////////////////////////////////////////////////////////////
    /**
     * 更新当前进度
     * @param progress [0-1]
     */
    public void setProgress(float progress) {
        mProgress = Math.max(0, Math.min(mMax, progress));
        mPercentTextPath.reset();
        mPathMeasure.setPath(mTextPath, false);
        //根据进度获取路径
        while (progress > mPathMeasure.getLength()) {
            mPathMeasure.getSegment(0, mPathMeasure.getLength(), mPercentTextPath, true);
            progress -= mPathMeasure.getLength();
            if (!mPathMeasure.nextContour()) {
                break;
            }
        }
        mPathMeasure.getSegment(0, progress, mPercentTextPath, true);
        if (mProgress == mMax) {
            if (mTextPaint.getStyle() != Paint.Style.FILL) {
                mTextPaint.setStyle(Paint.Style.FILL);
            }
        } else if (mTextPaint.getStyle() != Paint.Style.STROKE){
            mTextPaint.setStyle(Paint.Style.STROKE);
        }
        invalidate();
    }

    /**
     * 设置显示文字
     * @param text
     */
    public void setText(String text) {
        if (null == text) {
            text = "";
        }
        mText = text;
        isNeededHandleNewLine = true;
        calculatorMax();
    }

    /**
     * @see #setText(String)
     * @param resId
     */
    public void setText(int resId) {
        setText(getResources().getString(resId));
    }

    public void setTextColor(int color) {
        if (mTextPaint.getColor() != color) {
            mTextPaint.setColor(color);
        }
    }

    /**
     * Set the default text size to the given value, interpreted as "scaled
     * pixel" units.  This size is adjusted based on the current density and
     * user font size preference.
     *
     * <p>Note: if this TextView has the auto-size feature enabled than this function is no-op.
     *
     * @param size The scaled pixel size.
     *
     * @attr ref android.R.styleable#TextView_textSize
     */
    public void setTextSize(float size) {
        setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
    }

    /**
     * Set the default text size to a given unit and value. See {@link
     * TypedValue} for the possible dimension units.
     *
     * <p>Note: if this TextView has the auto-size feature enabled than this function is no-op.
     *
     * @param unit The desired dimension unit.
     * @param size The desired size in the given units.
     *
     * @attr ref android.R.styleable#TextView_textSize
     */
    public void setTextSize(int unit, float size) {
        setTextSizeInternal(unit, size, true /* shouldRequestLayout */);
    }

    private void setTextSizeInternal(int unit, float size, boolean shouldRequestLayout) {
        Context c = getContext();
        Resources r;

        if (c == null) {
            r = Resources.getSystem();
        } else {
            r = c.getResources();
        }

        setRawTextSize(TypedValue.applyDimension(unit, size, r.getDisplayMetrics()),
                shouldRequestLayout);
    }

    private void setRawTextSize(float size, boolean shouldRequestLayout) {
        if (size != mTextPaint.getTextSize()) {
            mTextPaint.setTextSize(size);

            if (shouldRequestLayout) {
                // Do not auto-size right after setting the text size.
                requestLayout();
                invalidate();
            }
        }
    }

    public void startAnim() {
        initAnim(0, 1, mRepeatMode, mRepeatCount);
    }

    public void startAnim(float start, float end, int repeatMode, int repeatCount) {
        initAnim(start, end, repeatMode, repeatCount);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void pauseAnim() {
        if (null != mValueAnimator) {
            mValueAnimator.pause();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void resueAnim() {
        if (null != mValueAnimator) {
            mValueAnimator.resume();
        }
    }

    public void stopAnim() {
        cancelAnimIfNeeded();
    }

    public void setRepeatMode(int repeatMode) {
        this.mRepeatMode = repeatMode;
    }

    public void setRepeatCount(int repeatCount) {
        this.mRepeatCount = repeatCount;
    }

}
