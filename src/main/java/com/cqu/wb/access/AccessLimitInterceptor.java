package com.cqu.wb.access;

import com.alibaba.fastjson.JSON;
import com.cqu.wb.domain.User;
import com.cqu.wb.redis.AccessKey;
import com.cqu.wb.redis.RedisService;
import com.cqu.wb.result.CodeMessage;
import com.cqu.wb.result.Result;
import com.cqu.wb.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by jingquan on 2018/8/10.
 */

/**
 * @description 拦截器进行相关数据验证
 */
@Service
public class AccessLimitInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisService redisService;

    /**
     *
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     * @description 使用拦截器在方法执行前进行'单位时间请求访问次数限制验证'与'用户登录信息验证'（返回true则执行方法，返回false则不执行）
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(handler instanceof HandlerMethod) {
            // 从分布式Session中取出用户信息并保存到UserContext（TreadLocal）中，后面方法参数解析器则从UserContext获取信息绑定到对应类型参数上
            User user = getUserFromSession(request, response);
            UserContext.setUser(user);

            // 获取修饰方法上的对应注解
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            AccessLimit accessLimit = handlerMethod.getMethodAnnotation(AccessLimit.class);
            // 若没有使用该注解
            if(accessLimit == null) {
                return true;
            }
            // 获取注解属性信息
            int seconds = accessLimit.seconds();
            int maxCount = accessLimit.maxCount();
            boolean needLogin = accessLimit.needLogin();

            // 用户登录信息验证
            if(needLogin == true) {
                if(user == null) {
                    renderErrorMessage(response, CodeMessage.SESSION_ERROR);
                    return false;
                }
            } else {
                // do nothing
            }

            // 单位时间请求访问次数限制验证
            String requestURI = request.getRequestURI();
            AccessKey keyPrefix = AccessKey.getAccessCountAccessKey(seconds);
            Integer accessCount = redisService.get(keyPrefix, requestURI + "_" + user.getId(), Integer.class);
            if(accessCount == null) {
                redisService.set(keyPrefix, requestURI + "_" + user.getId(), 1);
            } else if(accessCount < maxCount) {
                redisService.incr(keyPrefix, requestURI + "_" + user.getId());
            } else {
                renderErrorMessage(response, CodeMessage.ACCESS_LIMIT);
                return false;
            }
        }

        return true;
    }

    /**
     *
     * @param request
     * @param response
     * @return
     * @description 从分布式Session中取出用户信息
     */
    private User getUserFromSession(HttpServletRequest request, HttpServletResponse response) {
        String paramToken = request.getParameter(userService.TOKEN_NAME);       // 从Request中获取对应参数
        String cookieToken = getCookieValue(request, userService.TOKEN_NAME);   // 从Request的Cookie中获取值

        // 验证用户信息是否失效
        if(StringUtils.isEmpty(cookieToken) && StringUtils.isEmpty(paramToken)) {
            return null;
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
    private String getCookieValue(HttpServletRequest request, String tokenName) {
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

    /**
     *
     * @param response
     * @param codeMessage
     * @throws IOException
     * @description 通过输出流手动递交错误信息
     */
    private void renderErrorMessage(HttpServletResponse response, CodeMessage codeMessage) throws IOException {
        // 设置响应的ContentType，防止响应输出乱码
        response.setContentType("application/json;charset=UTF-8");

        OutputStream outputStream = response.getOutputStream();
        String outString = JSON.toJSONString(Result.error(codeMessage));
        outputStream.write(outString.getBytes());

        outputStream.flush();
        outputStream.close();
    }
}
