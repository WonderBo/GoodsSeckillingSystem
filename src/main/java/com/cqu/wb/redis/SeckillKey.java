package com.cqu.wb.redis;

/**
 * Created by jingquan on 2018/8/6.
 */
public class SeckillKey extends BasePrefix {

    public SeckillKey(int expireSeconds, String keyPrefix) {
        super(expireSeconds, keyPrefix);
    }

    public static SeckillKey isGoodsSeckillOverSeckillKey = new SeckillKey(0, "isGoodsSeckillOver");
}
