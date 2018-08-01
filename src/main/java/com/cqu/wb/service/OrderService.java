package com.cqu.wb.service;

import com.cqu.wb.dao.OrderDao;
import com.cqu.wb.domain.Order;
import com.cqu.wb.domain.SeckillOrder;
import com.cqu.wb.domain.User;
import com.cqu.wb.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * Created by jingquan on 2018/8/1.
 */
@Service
public class OrderService {

    @Autowired
    private OrderDao orderDao;

    /**
     *
     * @param userId
     * @param goodsId
     * @return
     * @description 根据用户ID和商品ID获取秒杀订单
     */
    public SeckillOrder getSeckillOrderByUserIdGoodsId(long userId, long goodsId) {
        return  orderDao.getSeckillOrderByUserIdGoodsId(userId, goodsId);
    }

    /**
     *
     * @param user
     * @param goodsVo
     * @return
     * @description 在order表和seckill_order表中创建订单
     */
    @Transactional
    public Order createOrder(User user, GoodsVo goodsVo) {
        Order order = new Order();
        order.setCreateDate(new Date());
        order.setDeliveryAddrId(0L);
        order.setGoodsCount(1);
        order.setGoodsId(goodsVo.getId());
        order.setGoodsName(goodsVo.getGoodsName());
        order.setGoodsPrice(goodsVo.getSeckillPrice());
        order.setOrderChannel(1);
        order.setStatus(0);
        order.setUserId(user.getId());
        orderDao.insertOrder(order);

        SeckillOrder seckillOrder = new SeckillOrder();
        seckillOrder.setGoodsId(goodsVo.getId());
        seckillOrder.setOrderId(order.getId());
        seckillOrder.setUserId(user.getId());
        orderDao.insertSeckillOrder(seckillOrder);

        return order;
    };
}
