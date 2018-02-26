package com.xiaosw.framework.model.bean;

import com.google.gson.annotations.SerializedName;

/**
 * @ClassName {@link AppH5UrlValue}
 * @Description
 *
 * @Date 2018-02-26.
 * @Author xiaosw<xiaosw0802@163.com>.
 */

public class AppH5UrlValue {
    @SerializedName("urlKey")
    private String key;
    @SerializedName("urlValue")
    private String value;
    @SerializedName("urlDesc")
    private String desc;// 描述

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public String getDesc() {
        return desc;
    }

    @Override
    public String toString() {
        return "AppH5UrlValue{" +
                "key='" + key + '\'' +
                ", value='" + value + '\'' +
                ", desc='" + desc + '\'' +
                '}';
    }
}
