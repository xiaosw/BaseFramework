package com.xiaosw.permission.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @ClassName {@link PermissionGrant}
 * @Description 权限拒绝
 *
 * @Date 2018-02-06.
 * @Author xiaosw<xiaosw0802@163.com>.
 */

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
public @interface PermissionGrant {

    int requestCode();

}
