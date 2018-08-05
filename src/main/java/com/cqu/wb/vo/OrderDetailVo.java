package com.cqu.wb.vo;

/**
 * Created by jingquan on 2018/8/5.
 */

import com.cqu.wb.domain.Order;

/**
 * @description 封装订单详情页面所需要的数据，用于页面静态化解析json数据
 */
public class OrderDetailVo {
    private GoodsVo goodsVo;
    private Order order;

    public GoodsVo getGoodsVo() {
        return goodsVo;
    }
    public void setGoodsVo(GoodsVo goodsVo) {
        this.goodsVo = goodsVo;
    }
    public Order getOrder() {
        return order;
    }
    public void setOrder(Order order) {
        this.order = order;
    }
}
