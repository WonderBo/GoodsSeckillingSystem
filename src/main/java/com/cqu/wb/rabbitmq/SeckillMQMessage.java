package com.cqu.wb.rabbitmq;

import com.cqu.wb.domain.User;

/**
 * Created by jingquan on 2018/8/6.
 */

/**
 * @description 消息队列中传递的秒杀封装消息
 */
public class SeckillMQMessage {

    private User user;
    private long goodsVoId;

    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }
    public long getGoodsVoId() {
        return goodsVoId;
    }
    public void setGoodsVoId(long goodsVoId) {
        this.goodsVoId = goodsVoId;
    }
}
