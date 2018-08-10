package com.cqu.wb.access;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by jingquan on 2018/8/10.
 */

/**
 * description 注解实现单位时间访问请求次数限制（包括用户登录实现）
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AccessLimit {

    int seconds();
    int maxCount();
    boolean needLogin() default true;
}
