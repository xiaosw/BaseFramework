package com.xiaosw.core.widget.wheel.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;

import com.xiaosw.core.R;
import com.xiaosw.core.widget.wheel.DateWheelView;

/**
 * @ClassName {@link BaseDateWheelDialog}
 * @Description
 *
 * @Date 2018-01-18.
 * @Author xiaosw<xiaosw0802@163.com>.
 */
public abstract class BaseDateWheelDialog extends AlertDialog {
    protected OnDateChangeListener mOnDateChangeListener;
    protected DateWheelView mDateWheelView;
    protected int mOriginalYear;
    protected int mOriginalMonth;
    protected int mOriginalDay;
    protected int mYear;
    protected int mMonth;
    protected int mDay;

    protected BaseDateWheelDialog(Context context) {
        this(context, R.style.DateWheelDialog);
    }

    protected BaseDateWheelDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected BaseDateWheelDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    protected Window setDefaultWindow(int layoutId) {
        return setDefaultWindow(getLayoutInflater().inflate(layoutId, null));
    }

    /**
     * set view to window display
     * @param view
     * @return
     */
    protected Window setDefaultWindow(View view) {
        final Window window = getWindow();
        window.setContentView(view);
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        // 设置弹出的动画效果
        window.setWindowAnimations(R.style.DateWheelAnim);
        window.setGravity(Gravity.BOTTOM);
        return window;
    }

    public BaseDateWheelDialog setCurrenetYear(int year) {
        if (mOriginalYear != year) {
            this.mOriginalYear = year;
        }
        return this;
    }

    public BaseDateWheelDialog setCurrenetMonth(int month) {
        if (mOriginalMonth != month) {
            this.mOriginalMonth = month;
        }
        return this;
    }

    public BaseDateWheelDialog setCurrenetDay(int day) {
        if (mOriginalDay != day) {
            this.mOriginalDay = day;
        }
        return this;
    }

    public BaseDateWheelDialog setOnDateChangeListener(OnDateChangeListener listener) {
        this.mOnDateChangeListener = listener;
        return this;
    }

    /**
     * linsen date change
     */
    public interface OnDateChangeListener {

        /**
         * cancel
         * @return true: user opreation dissmiss, false: auto dissmiss
         */
        boolean onCancel();

        /**
         *
         * @param year selected year
         * @param month selected month
         * @param day selected day
         * @return true: user opreation dissmiss, false: auto dissmiss
         */
        boolean onOk(int year, int month, int day);

    }
}
