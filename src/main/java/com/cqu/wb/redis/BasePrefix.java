package com.cqu.wb.redis;

/**
 * Created by jingquan on 2018/7/26.
 */

/**
 * @description 实现接口方法，具体描述如何区分Redis中的Key（接口-》抽象类-》实体类：抽象类起到实现公共方法作用）
 */
public abstract class BasePrefix implements KeyPrefix {
    private int expireSeconds;
    private String keyPrefix;

    public BasePrefix(String keyPrefix) {
        this(0, keyPrefix);     // 0代表永不过期
    }
    public BasePrefix(int expireSeconds, String keyPrefix) {
        this.expireSeconds = expireSeconds;
        this.keyPrefix = keyPrefix;
    }

    @Override
    public int getExpireSeconds() {
        return this.expireSeconds;
    }

    @Override
    public String getKeyPrefix() {
        String className = this.getClass().getSimpleName();
        return className + ":" +this.keyPrefix + ":";     // 在前缀前加上类名起到区分作用
    }
}
