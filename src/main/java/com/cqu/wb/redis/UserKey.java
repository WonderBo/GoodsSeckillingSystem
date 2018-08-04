package com.cqu.wb.redis;

/**
 * Created by jingquan on 2018/7/30.
 */
public class UserKey extends BasePrefix {

    public static final int DEFAULT_TOKEN_EXPIRE = 3600 * 24;     // 单位为秒（Redis中缓存值过期时间）

    public UserKey(String keyPrefix) {
        super(keyPrefix);
    }
    public UserKey(int expireSeconds, String keyPrefix) {
        super(expireSeconds, keyPrefix);
    }

    public static UserKey tokenUserKey = new UserKey(DEFAULT_TOKEN_EXPIRE, "token");
    public static UserKey idUserKey = new UserKey(0, "id");
}
