package com.cqu.wb.vo;

/**
 * Created by jingquan on 2018/7/29.
 */

// VO是值对象，精确讲它是业务对象，是存活在业务层的，是业务逻辑使用的，它存活的目的就是为数据提供一个生存的地方。

/**
 * @description 用户登录值对象（保存login.html用户登录请求信息）
 */
public class LoginVo {
    private String mobile;
    private String password;

    public String getMobile() {
        return mobile;
    }
    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "LoginVo [mobile=" + mobile + ", password=" + password + "]";
    }
}
