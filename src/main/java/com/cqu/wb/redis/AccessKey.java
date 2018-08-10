package com.cqu.wb.redis;

/**
 * Created by jingquan on 2018/8/10.
 */
public class AccessKey extends BasePrefix {

    private AccessKey(int expireSeconds, String keyPrefix) {
        super(expireSeconds, keyPrefix);
    }

    public static AccessKey accessCountAccessKey = new AccessKey(5, "accessCount");

    /**
     *
     * @param expireSeconds
     * @return
     * @description 参数动态设置的KEY前缀（用于动态设置某时间范围内的访问请求次数限制）
     */
    public static AccessKey getAccessCountAccessKey(int expireSeconds) {
        return new AccessKey(expireSeconds, "accessCountInExpireSeconds");
    }
}
