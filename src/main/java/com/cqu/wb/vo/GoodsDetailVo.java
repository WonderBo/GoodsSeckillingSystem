package com.cqu.wb.vo;

import com.cqu.wb.domain.User;

/**
 * Created by jingquan on 2018/8/4.
 */

/**
 * @description 封装商品详情页面所需要的数据，用于页面静态化解析json数据
 */
public class GoodsDetailVo {
    private int seckillStatus;
    private int remainSeconds;
    private GoodsVo goodsVo;
    private User user;

    public int getSeckillStatus() {
        return seckillStatus;
    }
    public void setSeckillStatus(int seckillStatus) {
        this.seckillStatus = seckillStatus;
    }
    public int getRemainSeconds() {
        return remainSeconds;
    }
    public void setRemainSeconds(int remainSeconds) {
        this.remainSeconds = remainSeconds;
    }
    public GoodsVo getGoodsVo() {
        return goodsVo;
    }
    public void setGoodsVo(GoodsVo goodsVo) {
        this.goodsVo = goodsVo;
    }
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }
}
