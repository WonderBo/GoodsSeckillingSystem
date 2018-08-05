package com.cqu.wb.redis;

/**
 * Created by jingquan on 2018/8/5.
 */
public class OrderKey extends BasePrefix {

    public OrderKey(String keyPrefix) {
        super(keyPrefix);
    }

    public static OrderKey userIdGoodsIdOrderKey = new OrderKey("userId_goodsId");
}
