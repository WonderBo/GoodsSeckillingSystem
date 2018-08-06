package com.cqu.wb.service;

import com.cqu.wb.domain.Order;
import com.cqu.wb.domain.SeckillOrder;
import com.cqu.wb.domain.User;
import com.cqu.wb.redis.RedisService;
import com.cqu.wb.redis.SeckillKey;
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

    @Autowired
    private RedisService redisService;

    /**
     *
     * @param user
     * @param goodsVo
     * @return
     * @description 减库存、下订单、写入秒杀订单（原子操作）
     */
    @Transactional
    public Order seckill(User user, GoodsVo goodsVo) {
        boolean result = goodsService.reduceStock(goodsVo);

        // 必须保证减库存成功后才可以下订单，事务只会保证发生异常时才会回滚，而减库存失败（库存为0）不是异常，而是更新操作不影响表记录
        if(result) {
            return orderService.createOrder(user, goodsVo);
        } else {
            setSeckillGoodsOver(goodsVo.getId());   // 在缓存中设置秒杀商品已经被秒杀完的标记
            return null;
        }
    }

    /**
     *
     * @param userId
     * @param goodsVoId
     * @return
     * @description 获取秒杀结果（是否生成秒杀订单）
     */
    public long getSeckillResult(long userId, long goodsVoId) {
        SeckillOrder seckillOrder = orderService.getSeckillOrderByUserIdGoodsId(userId, goodsVoId);
        if(seckillOrder != null) {  // 秒杀成功
            return seckillOrder.getOrderId();
        } else {
            boolean isSeckillOver = getSeckillGoodsOver(goodsVoId);     // 获取缓存中秒杀商品是否已经被秒杀完的标记
            if(isSeckillOver) {
                return -1;      // 秒杀失败
            } else {
                return 0;       // 排队中
            }
        }
    }

    /**
     *
     * @param goodsVoId
     * @description 在缓存中设置秒杀商品已经被秒杀完的标记
     */
    private void setSeckillGoodsOver(long goodsVoId) {
        redisService.set(SeckillKey.isGoodsSeckillOverSeckillKey, "" + goodsVoId, true);
    }

    /**
     *
     * @param goodsVoId
     * @return
     * @description 获取缓存中秒杀商品是否已经被秒杀完的标记
     */
    private boolean getSeckillGoodsOver(long goodsVoId) {
        return redisService.exists(SeckillKey.isGoodsSeckillOverSeckillKey, "" + goodsVoId);
    }
}
