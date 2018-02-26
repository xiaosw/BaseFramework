package com.xiaosw.framework.widget.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;

import com.xiaosw.core.widget.wheel.DateWheelView;
import com.xiaosw.core.widget.wheel.WheelView;
import com.xiaosw.core.widget.wheel.dialog.BaseDateWheelDialog;
import com.xiaosw.framework.R;

import java.util.Calendar;

/**
 * @ClassName {@link DateWheelDialog}
 * @Description
 *
 * @Date 2018-01-18.
 * @Author xiaosw<xiaosw0802@163.com>.
 */

public class DateWheelDialog extends BaseDateWheelDialog {

    public DateWheelDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setWindow();
        initView();
        maybeAdjustChild();
    }

    private void setWindow() {
        final Window window = setDefaultWindow(R.layout.dialog_date_wheel);
        mDateWheelView = (DateWheelView) window.findViewById(R.id.date_wheel);
        mDateWheelView.setOnDateWheelDataChangeListener(new DateWheelView.OnDateWheelDataChangeListener() {
            @Override
            public void onDateChange(WheelView wheelView, int year, int month, int day, int timeUnit, int hour, int minute, int second) {
                mYear = year;
                mMonth = month;
                mDay = day;
            }
        });
        mYear = mDateWheelView.getValueWithKey(Calendar.YEAR);
        mMonth = mDateWheelView.getValueWithKey(Calendar.MONTH);
        mDay = mDateWheelView.getValueWithKey(Calendar.DAY_OF_MONTH);
    }

    private void initView() {
        findViewById(R.id.bt_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mOnDateChangeListener) {
                    if (!mOnDateChangeListener.onCancel()) {
                        dismiss();
                    }
                } else {
                    dismiss();
                }
            }
        });
        findViewById(R.id.bt_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mOnDateChangeListener) {
                    if (!mOnDateChangeListener.onOk(mYear, mMonth, mDay)) {
                        dismiss();
                    }
                } else {
                    dismiss();
                }
            }
        });
        if (mOriginalYear > 0) {
            mDateWheelView.setYear(mOriginalYear);
        }

        if (mOriginalMonth > 0) {
            mDateWheelView.setMonth(mOriginalMonth);
        }

        if(mOriginalDay > 0) {
            mDateWheelView.setDay(mOriginalDay);
        }
    }

    /**
     * 根据具体需求调整滚轮间距，wheel默认
     */
    private void maybeAdjustChild() {
        ViewGroup.LayoutParams layoutParams =
                mDateWheelView.getChildAt(1).getLayoutParams();
        if (null != layoutParams && layoutParams instanceof LinearLayout.LayoutParams) {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) layoutParams;
            int screenWidth = getContext().getResources().getDisplayMetrics().widthPixels;
            // 为了不修改WheelView源码，显示部分在这里直接按经验值处理
            // screenWidth - (yearWidth + monthWidth + dayWidth)
            int span = screenWidth - 640;
            if (span > 0) {
                int margin = span / 4;
                params.leftMargin = margin;
                params.rightMargin = margin;
            }

        }
    }
}
