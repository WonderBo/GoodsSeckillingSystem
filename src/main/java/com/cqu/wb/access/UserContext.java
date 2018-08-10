package com.cqu.wb.access;

import com.cqu.wb.domain.User;

/**
 * Created by jingquan on 2018/8/10.
 */

/**
 * @description 使用ThreadLocal保存／取出用户信息
 */
public class UserContext {

    private static ThreadLocal<User> userThreadLocal = new ThreadLocal<User>();

    public static void setUser(User user) {
        userThreadLocal.set(user);
    }

    public static User getUser() {
        return userThreadLocal.get();
    }
}
