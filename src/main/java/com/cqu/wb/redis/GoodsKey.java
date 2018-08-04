package com.cqu.wb.redis;

/**
 * Created by jingquan on 2018/8/4.
 */
public class GoodsKey extends BasePrefix {

    // HTML页面缓存一般是为应对页面短时高并发访问带来的巨大流量和带宽要求，为保存页面数据的一致性，缓存时间不能太长
    public static final int DEFAULT_HTML_EXPIRE = 60;     // 单位为秒（Redis中缓存值过期时间）

    public GoodsKey(int expireSeconds, String keyPrefix) {
        super(expireSeconds, keyPrefix);
    }

    public static GoodsKey goodsListHtmlGoodsKey = new GoodsKey(DEFAULT_HTML_EXPIRE, "goodsListHtml");
    public static GoodsKey goodsDetailHtmlGoodsKey = new GoodsKey(DEFAULT_HTML_EXPIRE, "goodsDetailHtml");
}
