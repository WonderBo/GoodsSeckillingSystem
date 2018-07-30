package com.cqu.wb.controller;

import com.cqu.wb.domain.User;
import com.cqu.wb.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;

/**
 * Created by jingquan on 2018/7/30.
 */
@Controller
@RequestMapping(value = "/goods")
public class GoodsController {

    @Autowired
    private UserService userService;

    /**
     *
     * @param response
     * @param model
     * @param cookieToken
     * @param paramToken
     * @return
     * @description 从分布式Session中获取用户信息，并跳转到商品列表页面
     *              由于每个页面都需要User的session信息，此方式获取Session用户信息代码冗余，推荐采用如下请求参数绑定的方式保存User信息
     */
    @RequestMapping(value = "/to_list_old", method = RequestMethod.GET)
    public String listGoodsOld(HttpServletResponse response, Model model,
                               @CookieValue(value = UserService.TOKEN_NAME, required = false)String cookieToken,
                               @RequestParam(value = UserService.TOKEN_NAME, required = false)String paramToken) {
        // 若用户信息失效则返回登录页面
        if(StringUtils.isEmpty(cookieToken) && StringUtils.isEmpty(paramToken)) {
            return "login";
        }

        // 从请求参数（为兼容手机端）或者Cookie中获取用户Token，优先考虑从请求参数中获取，若无再考虑Cookie中获取
        String token = null;
        if(StringUtils.isEmpty(paramToken)) {
            token = cookieToken;
        } else {
            token = paramToken;
        }
        // 根据Token从Redis缓存中获取用户信息
        User user = userService.getUserByToken(response, token);
        model.addAttribute("user", user);

        return "goods_list";
    }
    /**
     *
     * @param model
     * @param user
     * @return
     * @description 采用请求参数解析绑定的方式，从分布式Session中获取用户信息并绑定到方法入参中
     */
    @RequestMapping(value = "/to_list")
    public String listGoodsNew(Model model, User user) {
        model.addAttribute("user", user);
        return "goods_list";
    }
}
