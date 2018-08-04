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

    // Service只能访问自己对应的DAO，不能访问其他模块的DAO（比如：跨过缓存访问处理），如果有需要，则只能访问其他模块的Service
    @Autowired
    private RedisService redisService;

    /**
     *
     * @param id
     * @return
     * @description 使用对象级缓存，根据用户id获取用户信息，若缓存未命中，则访问数据库并进行缓存。
     *              对象级缓存的粒度比较小，对象级缓存和页面级缓存主要区别在于：对象级缓存有效时间比较长，
     *              为保证对象缓存数据的一致性，当数据发生更新，需要进行缓存失效管理（对应缓存数据进行更新或者修改操作），
     *              而页面级缓存有效时间比较短，到达失效时间时Redis会自动删除失效缓存
     */
    public User getUserById(long id) {
        // 访问缓存
        User user = redisService.get(UserKey.idUserKey, "" + id, User.class);
        if(user != null) {
            return user;
        }

        // 缓存未命中则访问数据库，并进行缓存
        user = userDao.getUserById(id);
        if(user != null) {
            redisService.set(UserKey.idUserKey, "" + id, user);
        }

        return user;
    }

    /**
     *
     * @param token
     * @param id
     * @param formPassword
     * @return
     * @description 更新用户密码操作：先更新数据库对应数据，再更新缓存数据
     *              参考文章：http://blog.csdn.net/tTU1EvLDeLFq5btqiK/article/details/78693323
     */
    public boolean updateUserPassword(String token, long id, String formPassword) {
        // 根据用户id得到用户
        User user = getUserById(id);
        if(user == null) {
            throw new GlobalException(CodeMessage.MOBILE_NOT_EXIT);
        }

        // 更新数据库（创建新对象进行增量更新，如果修改原对象进行全量更新会降低效率）
        User updatedUser = new User();
        updatedUser.setId(id);
        updatedUser.setPassword(MD5Util.formPassToDBPass(formPassword, user.getSalt()));
        userDao.updateUserPassword(updatedUser);

        // 处理缓存（更新缓存只能进行全量更新，缓存更新相当于覆盖原记录，否则部分数据会丢失）
        redisService.delete(UserKey.idUserKey, "" + id);    // 删除缓存对应记录
        user.setPassword(updatedUser.getPassword());
        // 由于分布式Session根据token在缓存中得到User，因此对应缓存记录只能覆盖，不能直接删除
        redisService.set(UserKey.tokenUserKey, token, user);    // （全量）覆盖／修改缓存对应记录

        return true;
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
