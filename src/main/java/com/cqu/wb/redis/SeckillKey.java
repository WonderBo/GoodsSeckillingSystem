package com.cqu.wb.redis;

/**
 * Created by jingquan on 2018/8/6.
 */
public class SeckillKey extends BasePrefix {

    public SeckillKey(int expireSeconds, String keyPrefix) {
        super(expireSeconds, keyPrefix);
    }

    public static SeckillKey isGoodsSeckillOverSeckillKey = new SeckillKey(0, "isGoodsSeckillOver");
    public static SeckillKey seckillPathSeckillKey = new SeckillKey(60, "seckillPath");
    public static SeckillKey seckillVerifyCodeSeckillKey = new SeckillKey(120, "seckillVerifyCode");
}
