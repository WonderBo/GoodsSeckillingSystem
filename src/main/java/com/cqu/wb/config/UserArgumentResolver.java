package com.cqu.wb.config;

import com.cqu.wb.domain.User;
import com.cqu.wb.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by jingquan on 2018/7/30.
 */

/**
 * @description 用户类型参数解析、绑定
 */
@Service
public class UserArgumentResolver implements HandlerMethodArgumentResolver {

    @Autowired
    private UserService userService;

    /**
     *
     * @param methodParameter
     * @return
     * @description 校验请求参数类型，如果类型符合才进行参数解析绑定
     */
    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        Class<?> clazz = methodParameter.getParameterType();
        if(clazz == User.class) {
            return true;
        }
        return false;
    }

    /**
     *
     * @param methodParameter
     * @param modelAndViewContainer
     * @param nativeWebRequest
     * @param webDataBinderFactory
     * @return
     * @throws Exception
     * @description 从分布式Session中获取用户信息，并绑定到Controller层方法中的对应类型参数中
     */
    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {
        // 获取Request与Response
        HttpServletRequest request = nativeWebRequest.getNativeRequest(HttpServletRequest.class);
        HttpServletResponse response = nativeWebRequest.getNativeResponse(HttpServletResponse.class);

        String paramToken = request.getParameter(userService.TOKEN_NAME);       // 从Request中获取对应参数
        String cookieToken = getCookieValue(request, userService.TOKEN_NAME);   // 从Request的Cookie中获取值

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

        return user;
    }

    /**
     *
     * @param request
     * @param tokenName
     * @return
     * @description 遍历请求中的Cookie，获取jsession的Cookie中的token值
     */
    public String getCookieValue(HttpServletRequest request, String tokenName) {
        Cookie[] cookies = request.getCookies();
        if(cookies == null || cookies.length <= 0) {
            return null;
        }

        for(Cookie cookie : cookies) {
            if(cookie.getName().equals(tokenName)) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
