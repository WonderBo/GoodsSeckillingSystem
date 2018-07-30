package com.cqu.wb.service;

import com.cqu.wb.dao.UserDao;
import com.cqu.wb.domain.User;
import com.cqu.wb.exception.GlobalException;
import com.cqu.wb.redis.RedisService;
import com.cqu.wb.redis.UserKey;
import com.cqu.wb.result.CodeMessage;
import com.cqu.wb.util.MD5Util;
import com.cqu.wb.util.UUIDUtil;
import com.cqu.wb.vo.LoginVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by jingquan on 2018/7/29.
 */
@Service
public class UserService {

    public static final String TOKEN_NAME = "jsession";

    @Autowired
    private UserDao userDao;

    @Autowired
    private RedisService redisService;

    /**
     *
     * @param id
     * @return
     * @description 根据用户id获取用户信息
     */
    public User getUserById(long id) {
        // 从数据库中取信息
        User user = userDao.getUserById(id);
        return user;
    }

    /**
     *
     * @param loginVo
     * @return
     * @description 用户登录验证
     */
    public String login(HttpServletResponse response, LoginVo loginVo) {
        // 用户输入验证
        if(loginVo == null) {
            throw new GlobalException(CodeMessage.SERVER_ERROR);
        }

        String mobile = loginVo.getMobile();
        String formPass = loginVo.getPassword();
        User user = getUserById(Long.parseLong(mobile));

        // 数据库验证
        // 判断手机号是否存在
        if(user == null) {
            throw new GlobalException(CodeMessage.MOBILE_NOT_EXIT);
        }
        // 验证密码
        String dbSalt = user.getSalt();
        String dbPass = user.getPassword();
        String expectedPass = MD5Util.formPassToDBPass(formPass, dbSalt);
        if(!dbPass.equals(expectedPass)) {
            throw new GlobalException(CodeMessage.PASSWORD_ERROR);
        }

        // 实现分布式Session
        String token = UUIDUtil.getUuid();
        setDistributedSession(response, token, user);

        return token;
    }

    /**
     *
     * @param response
     * @param token
     * @param user
     * @description 实现分布式Session（Redis缓存 + Cookie）
     */
    public void setDistributedSession(HttpServletResponse response, String token, User user) {
        // Redis中缓存Session信息（token／User 对）
        redisService.set(UserKey.tokenUserKey, token, user);

        // 将token信息保存进Cookie中并传送给用户浏览器
        Cookie cookie = new Cookie(TOKEN_NAME, token);
        cookie.setMaxAge(UserKey.tokenUserKey.getExpireSeconds());
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    /**
     *
     * @param token
     * @return
     * @description 分布式Session根据token值在Redis中取出用户信息
     */
    public User getUserByToken(HttpServletResponse response, String token) {
        // 输入验证
        if(StringUtils.isEmpty(token)) {
            return null;
        }

        User user = redisService.get(UserKey.tokenUserKey, token, User.class);
        // 在分布式Session（Redis和Cookie）中延长有效期
        if(user != null) {
            setDistributedSession(response, token, user);
        }

        return user;
    }
}
