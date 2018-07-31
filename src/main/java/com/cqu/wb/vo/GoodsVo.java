package com.cqu.wb.vo;

import com.cqu.wb.domain.Goods;

import java.util.Date;

/**
 * Created by jingquan on 2018/7/31.
 */

/**
 * @description 秒杀商品信息值对象（整合有固定商品信息与秒杀商品信息）
 */
public class GoodsVo extends Goods {
    private Double seckillPrice;
    private Integer stockCount;
    private Date startDate;
    private Date endDate;

    public Double getSeckillPrice() {
        return seckillPrice;
    }
    public void setSeckillPrice(Double seckillPrice) {
        this.seckillPrice = seckillPrice;
    }
    public Integer getStockCount() {
        return stockCount;
    }
    public void setStockCount(Integer stockCount) {
        this.stockCount = stockCount;
    }
    public Date getStartDate() {
        return startDate;
    }
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }
    public Date getEndDate() {
        return endDate;
    }
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}
