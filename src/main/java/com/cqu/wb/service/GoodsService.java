package com.cqu.wb.service;

import com.cqu.wb.dao.GoodsDao;
import com.cqu.wb.domain.SeckillGoods;
import com.cqu.wb.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by jingquan on 2018/7/31.
 */
@Service
public class GoodsService {

    @Autowired
    private GoodsDao goodsDao;

    /**
     *
     * @return
     * @description 获取秒杀商品信息列表
     */
    public List<GoodsVo> listGoodsVo() {
        return goodsDao.listGoodsVo();
    }

    /**
     *
     * @param goodsId
     * @return
     * @description 根据商品ID获取秒杀商品信息
     */
    public GoodsVo getGoodsVoByGoodsId(long goodsId) {
        return goodsDao.getGoodsVoByGoodsId(goodsId);
    }

    /**
     *
     * @param goodsVo
     * @return
     * @description 减少秒杀商品库存（存在GoodsVo到SeckillGoods到转化）
     */
    public boolean reduceStock(GoodsVo goodsVo) {
        SeckillGoods seckillGoods = new SeckillGoods();
        seckillGoods.setGoodsId(goodsVo.getId());
        int result = goodsDao.reduceStock(seckillGoods);

        return result > 0;
    }

    /**
     *
     * @param goodsVoList
     * @description 重置秒杀商品库存
     */
    public void resetStock(List<GoodsVo> goodsVoList) {
        for(GoodsVo goodsVo : goodsVoList) {
            SeckillGoods seckillGoods = new SeckillGoods();
            seckillGoods.setGoodsId(goodsVo.getId());
            seckillGoods.setStockCount(goodsVo.getStockCount());

            goodsDao.resetStock(seckillGoods);
        }
    }
}
