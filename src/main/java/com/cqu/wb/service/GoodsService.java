package com.cqu.wb.service;

import com.cqu.wb.dao.GoodsDao;
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
}
