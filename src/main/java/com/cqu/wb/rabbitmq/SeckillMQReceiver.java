package com.cqu.wb.rabbitmq;

import com.cqu.wb.domain.SeckillOrder;
import com.cqu.wb.domain.User;
import com.cqu.wb.service.GoodsService;
import com.cqu.wb.service.OrderService;
import com.cqu.wb.service.SeckillService;
import com.cqu.wb.util.JSONUtil;
import com.cqu.wb.vo.GoodsVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by jingquan on 2018/8/6.
 */
@Service
public class SeckillMQReceiver {

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private SeckillService seckillService;

    private static Logger logger = LoggerFactory.getLogger(SeckillMQReceiver.class);

    /**
     *
     * @param message
     * @description 从消息队列接收秒杀消息并处理消息
     */
    @RabbitListener(queues = SeckillMQConfig.SECKILL_QUEUE)
    public void consumeSeckillMessage(String message) {
        logger.info("接收秒杀消息：" + message);

        // 解析消息
        SeckillMQMessage seckillMQMessage = JSONUtil.stringToBean(message, SeckillMQMessage.class);
        User user = seckillMQMessage.getUser();
        long goodsVoId = seckillMQMessage.getGoodsVoId();
        GoodsVo goodsVo = goodsService.getGoodsVoByGoodsId(goodsVoId);

        // 判断库存剩余数量（虽然缓存进行预减库存并判断，但是由于缓存不能保证操作的原子性，当并发很大，缓存数量可能为负，因此需要数据库再次判断）
        int stock = goodsVo.getStockCount();
        if(stock <= 0) {
            return;
        }

        // 判断是否秒杀重复（与SeckillController均是从缓存中取值判断，因此此步不为必须）
        SeckillOrder seckillOrder = orderService.getSeckillOrderByUserIdGoodsId(user.getId(), goodsVoId);
        if(seckillOrder != null) {
            return;
        }

        // 实际业务逻辑：减库存 下订单 写入秒杀订单
        seckillService.seckill(user, goodsVo);
    }
}
