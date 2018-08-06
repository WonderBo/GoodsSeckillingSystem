package com.cqu.wb.rabbitmq;

import com.cqu.wb.util.JSONUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by jingquan on 2018/8/6.
 */
@Service
public class SeckillMQSender {

    private static Logger logger = LoggerFactory.getLogger(SeckillMQSender.class);

    @Autowired
    private AmqpTemplate amqpTemplate;

    /**
     *
     * @param seckillMessage
     * @description 向消息队列发送秒杀消息
     */
    public void sendSeckillMessage(SeckillMQMessage seckillMessage) {
        String message = JSONUtil.beanTOString(seckillMessage);
        logger.info("发送秒杀信息：" + message);

        amqpTemplate.convertAndSend(SeckillMQConfig.SECKILL_QUEUE, message);
    }
}
