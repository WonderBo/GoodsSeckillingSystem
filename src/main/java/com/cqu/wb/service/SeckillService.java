package com.cqu.wb.service;

import com.cqu.wb.domain.Order;
import com.cqu.wb.domain.User;
import com.cqu.wb.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by jingquan on 2018/8/1.
 */
@Service
public class SeckillService {

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private OrderService orderService;

    /**
     *
     * @param user
     * @param goodsVo
     * @return
     * @description 减库存、下订单、写入秒杀订单（原子操作）
     */
    @Transactional
    public Order seckill(User user, GoodsVo goodsVo) {
        goodsService.reduceStock(goodsVo);
        return orderService.createOrder(user, goodsVo);
    }
}
