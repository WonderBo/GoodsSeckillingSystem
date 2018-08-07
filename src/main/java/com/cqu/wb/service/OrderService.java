package com.cqu.wb.service;

import com.cqu.wb.dao.OrderDao;
import com.cqu.wb.domain.Order;
import com.cqu.wb.domain.SeckillOrder;
import com.cqu.wb.domain.User;
import com.cqu.wb.redis.OrderKey;
import com.cqu.wb.redis.RedisService;
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

    @Autowired
    private RedisService redisService;

    /**
     *
     * @param userId
     * @param goodsId
     * @return
     * @description 根据用户ID和商品ID获取秒杀订单
     */
    public SeckillOrder getSeckillOrderByUserIdGoodsId(long userId, long goodsId) {
        // return  orderDao.getSeckillOrderByUserIdGoodsId(userId, goodsId);

        // 直接从缓存中读取判断是否秒杀重复
        return redisService.get(OrderKey.userIdGoodsIdOrderKey, userId + "_" + goodsId, SeckillOrder.class);
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

        // 将秒杀订单存入缓存，用于优化判断是否秒杀重复（订单重复）
        redisService.set(OrderKey.userIdGoodsIdOrderKey, user.getId() + "_" + goodsVo.getId(), seckillOrder);

        return order;
    }

    /**
     *
     * @param orderId
     * @return
     * @description 根据订单ID获取订单信息
     */
    public Order getOrderById(long orderId) {
        return orderDao.getOrderById(orderId);
    }

    /**
     * @description 删除订单表和秒杀订单表所有记录
     */
    public void deleteOrders() {
        orderDao.deleteOrders();
        orderDao.deleteSeckillOrders();
    }
}
