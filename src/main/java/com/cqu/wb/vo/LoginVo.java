package com.cqu.wb.vo;

/**
 * Created by jingquan on 2018/7/29.
 */

// VO是值对象，准确讲它是存活于业务层的业务对象，被业务逻辑使用的，它存活的目的就是为数据提供一个生存的地方（页面与控制层的交互介质）

import com.cqu.wb.validator.IsMobile;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

/**
 * @description 用户登录值对象（保存login.html用户登录请求信息）
 */
public class LoginVo {

    @NotNull
    @IsMobile
    private String mobile;
    @NotNull
    @Length(min = 32)
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
