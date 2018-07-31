package com.cqu.wb.domain;

/**
 * Created by jingquan on 2018/7/31.
 */
public class Goods {
    private Long id;
    private String goodsName;
    private String goodsTitle;
    private String goodsImage;
    private String goodsDetail;
    private Double goodsPrice;
    private Integer goodsStock;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getGoodsName() {
        return goodsName;
    }
    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }
    public String getGoodsTitle() {
        return goodsTitle;
    }
    public void setGoodsTitle(String goodsTitle) {
        this.goodsTitle = goodsTitle;
    }
    public String getGoodsImage() {
        return goodsImage;
    }
    public void setGoodsImage(String goodsImage) {
        this.goodsImage = goodsImage;
    }
    public String getGoodsDetail() {
        return goodsDetail;
    }
    public void setGoodsDetail(String goodsDetail) {
        this.goodsDetail = goodsDetail;
    }
    public Double getGoodsPrice() {
        return goodsPrice;
    }
    public void setGoodsPrice(Double goodsPrice) {
        this.goodsPrice = goodsPrice;
    }
    public Integer getGoodsStock() {
        return goodsStock;
    }
    public void setGoodsStock(Integer goodsStock) {
        this.goodsStock = goodsStock;
    }
}
