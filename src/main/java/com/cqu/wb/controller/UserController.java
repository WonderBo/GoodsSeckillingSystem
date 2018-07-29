package com.cqu.wb.controller;

import com.cqu.wb.result.CodeMessage;
import com.cqu.wb.result.Result;
import com.cqu.wb.service.UserService;
import com.cqu.wb.util.ValidatorUtil;
import com.cqu.wb.vo.LoginVo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;

/**
 * Created by jingquan on 2018/7/29.
 */
@Controller
@RequestMapping(value = "/user")
public class UserController {

    private static Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    /**
     *
     * @return
     * @descrption 跳转到用户登录页面
     */
    @RequestMapping(value = "/to_login", method = RequestMethod.GET)
    public String toLogin() {
        return "login";
    }

    /**
     *
     * @param loginVo
     * @return
     * @description 用户登录验证
     */
    @RequestMapping(value = "/do_login", method = RequestMethod.POST)
    @ResponseBody
    public Result<Boolean> doLogin(@Valid LoginVo loginVo) {
        logger.info(loginVo.toString());

/*
        // 页面参数校验（手工校验），推荐使用JSR303参数校验框架
        String mobile = loginVo.getMobile();
        String formPass = loginVo.getPassword();
        if(StringUtils.isEmpty(formPass)) {
            return Result.error(CodeMessage.PASSWORD_EMPTY);
        }
        if(StringUtils.isEmpty(mobile)) {
            return Result.error(CodeMessage.MOBILE_EMPTY);
        }
        if(ValidatorUtil.isMobile(mobile)) {
            return Result.error(CodeMessage.MOBILE_ERROR);
        }
*/

        // 登录功能
        CodeMessage codeMessage = userService.login(loginVo);
        if(codeMessage.getCode() == 0) {
            return Result.success(true);
        } else {
            return Result.error(codeMessage);
        }
    }
}
