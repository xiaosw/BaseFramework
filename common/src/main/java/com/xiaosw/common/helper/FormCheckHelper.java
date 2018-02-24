package com.xiaosw.common.helper;

import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaosw.common.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName {@link FormCheckHelper}
 * @Description 表单检测帮助类。用于页面需要填写必填项才能下一步等操作。
 *
 * @Date 2018-02-24.
 * @Author xiaosw<xiaosw0802@163.com>.
 */

public class FormCheckHelper implements TextWatcher, CompoundButton.OnCheckedChangeListener {

    private static final String TAG = "FormCheckHelper";

    private View mNext;
    private OnFormSubmitListener mOnFormSubmitListener;
    private List<View> mConditions;

    public FormCheckHelper(View next) {
        this(next, false);
    }

    public FormCheckHelper(View next, boolean enable) {
        this.mNext = next;
        this.mNext.setEnabled(enable);
        mConditions = new ArrayList<>();
    }

    public void add(TextView textView) {
        if (null == textView) {
            LogUtil.w(TAG, "add: textView is null!");
            return;
        }
        textView.addTextChangedListener(this);
        mConditions.add(textView);
    }

    public void add(CheckBox checkBox) {
        if (null == checkBox) {
            LogUtil.w(TAG, "add: checkBox is null!");
            return;
        }
        checkBox.setOnCheckedChangeListener(this);
        mConditions.add(checkBox);
    }

    public void add(ImageView imageView) {
        if (null == imageView) {
            LogUtil.w(TAG, "add: imageView is null!");
            return;
        }
        mConditions.add(imageView);
    }

    public void setOnFormSubmitListener(OnFormSubmitListener listener) {
        this.mOnFormSubmitListener = listener;
        if (null != mOnFormSubmitListener) {
            mNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnFormSubmitListener.onFormSubmit();
                }
            });
        } else {
            mNext.setOnClickListener(null);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        checkEnable();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        checkEnable();
    }

    private void checkEnable() {
        boolean isEnable = true;
        for (View conditionView : mConditions) {
            if (conditionView instanceof CheckBox) {
                if (!((CheckBox) conditionView).isChecked()) {
                    isEnable = false;
                    break;
                }
            } else if (conditionView instanceof TextView) {
                String text = ((TextView) conditionView).getText().toString();
                if (TextUtils.isEmpty(text)) {
                    isEnable = false;
                    break;
                }
            } else if (conditionView instanceof ImageView) {
                if (((ImageView) conditionView).getDrawable() == null) {
                    isEnable = false;
                    break;
                }
            }
        }
        mNext.setEnabled(isEnable);
    }

    interface OnFormSubmitListener {
        void onFormSubmit();
    }
}
