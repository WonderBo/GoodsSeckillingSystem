package com.cqu.wb.service;

import com.cqu.wb.dao.UserDao;
import com.cqu.wb.domain.User;
import com.cqu.wb.result.CodeMessage;
import com.cqu.wb.util.MD5Util;
import com.cqu.wb.vo.LoginVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by jingquan on 2018/7/29.
 */
@Service
public class UserService {
    @Autowired
    private UserDao userDao;

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
    public CodeMessage login(LoginVo loginVo) {
        // 用户输入验证
        if(loginVo == null) {
            return CodeMessage.SERVER_ERROR;
        }

        String mobile = loginVo.getMobile();
        String formPass = loginVo.getPassword();
        User user = getUserById(Long.parseLong(mobile));

        // 数据库验证
        // 判断手机号是否存在
        if(user == null) {
            return CodeMessage.MOBILE_NOT_EXIT;
        }
        // 验证密码
        String dbSalt = user.getSalt();
        String dbPass = user.getPassword();
        String expectedPass = MD5Util.formPassToDBPass(formPass, dbSalt);
        if(!dbPass.equals(expectedPass)) {
            return CodeMessage.PASSWORD_ERROR;
        }

        return CodeMessage.SUCCESS;
    }
}
