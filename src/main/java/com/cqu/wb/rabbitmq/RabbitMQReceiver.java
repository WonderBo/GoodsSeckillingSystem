package com.cqu.wb.rabbitmq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

/**
 * Created by jingquan on 2018/8/5.
 */

/**
 * @description 消息队列消费者与消息队列Queue存在对应操作绑定关系
 *              RabbitMQListener会自动监听消息队列并接收其中到消息
 */
@Service
public class RabbitMQReceiver {

    private static Logger logger = LoggerFactory.getLogger(RabbitMQReceiver.class);

    /**
     *
     * @param message
     * @description 消息消费者监听对应消息队列并接收消息
     */
    @RabbitListener(queues = RabbitMQConfig.DIRECT_QUEUE)
    public void receiveDirect(String message) {
        logger.info("DIRECT_QUEUE 收到消息：" + message);
    }

    /**
     *
     * @param message
     * @description 消息消费者监听对应消息队列并接收消息
     */
    @RabbitListener(queues = RabbitMQConfig.TOPIC_QUEUE_1)
    public void receiveTopic1(String message) {
        logger.info("TOPIC_QUEUE_1 收到消息：" + message);
    }
    @RabbitListener(queues = RabbitMQConfig.TOPIC_QUEUE_2)
    public void receiveTopic2(String message) {
        logger.info("TOPIC_QUEUE_2 收到消息：" + message);
    }

    /**
     *
     * @param message
     * @description 消息消费者监听对应消息队列并接收消息
     */
    @RabbitListener(queues = RabbitMQConfig.FANOUT_QUEUE_1)
    public void receiveFanout1(String message) {
        logger.info("FANOUT_QUEUE_1 收到消息：" + message);
    }
    @RabbitListener(queues = RabbitMQConfig.FANOUT_QUEUE_2)
    public void receiveFanout2(String message) {
        logger.info("FANOUT_QUEUE_2 收到消息：" + message);
    }

    /**
     *
     * @param bytes
     * @description 消息消费者监听对应消息队列并接收消息
     */
    @RabbitListener(queues = RabbitMQConfig.HEADERS_QUEUE)
    public void receiveHeader(byte[] bytes) {
        logger.info("HEADERS_QUEUE 收到消息：" + new String(bytes));
    }
}
