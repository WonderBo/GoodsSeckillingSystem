package com.cqu.wb.rabbitmq;

import com.cqu.wb.util.JSONUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by jingquan on 2018/8/5.
 */

/**
 * @description 消息队列生产者与交换机Exchange存在对应操作绑定关系
 *              Direct模式可以使用RabbitMQ自带的Exchange：default Exchange,所以不需要将Exchange进行任何绑定(binding)操作）
 */
@Service
public class RabbitMQSender {

    private static Logger logger = LoggerFactory.getLogger(RabbitMQSender.class);

    // Spring提供的操作RabbitMQ的模版类
    @Autowired
    private AmqpTemplate amqpTemplate;

    /**
     *
     * @param object
     * @description 消息生产者向消息队列发送消息（Direct模式）
     */
    public void sendDirect(Object object) {
        String message = JSONUtil.beanTOString(object);
        amqpTemplate.convertAndSend(RabbitMQConfig.DIRECT_QUEUE, message);

        logger.info("向 DIRECT_QUEUE 发送消息：" + message);
    }

    /**
     *
     * @param object
     * @description 消息生产者向消息队列发送消息（Topic模式）
     */
    public void sendTopic(Object object) {
        String message = JSONUtil.beanTOString(object);
        amqpTemplate.convertAndSend(RabbitMQConfig.TOPIC_EXCHANGE, "Topic.Routing.Key.1", message + "_1");
        amqpTemplate.convertAndSend(RabbitMQConfig.TOPIC_EXCHANGE, "Topic.Routing.Key.2", message + "_2");

        logger.info("向 TOPIC_EXCHANGE 发送消息：" + message);
    }

    /**
     *
     * @param object
     * @description 消息生产者向消息队列发送消息（Fanout模式）
     */
    public void sendFanout(Object object) {
        String message = JSONUtil.beanTOString(object);
        amqpTemplate.convertAndSend(RabbitMQConfig.FANOUT_EXCHANGE, "", message);

        logger.info("向 FANOUT_EXCHANGE 发送消息：" + message);
    }

    /**
     *
     * @param object
     * @description 消息生产者向消息队列发送消息（Header模式）
     */
    public void sendHeaders(Object object) {
        String message = JSONUtil.beanTOString(object);
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setHeader("FILTER_HEADER_KEY_1", "FILTER_HEADER_VALUE_1");
        messageProperties.setHeader("FILTER_HEADER_KEY_2", "FILTER_HEADER_VALUE_2");
        Message messageObject = new Message(message.getBytes(), messageProperties);
        amqpTemplate.convertAndSend(RabbitMQConfig.HEADERS_EXCHANGE, "", messageObject);

        logger.info("向 HEADERS_EXCHANGE 发送消息：" + message);
    }
}
