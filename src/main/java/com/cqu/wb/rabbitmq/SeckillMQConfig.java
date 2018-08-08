package com.cqu.wb.rabbitmq;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by jingquan on 2018/8/6.
 */
@Configuration
public class SeckillMQConfig {

    public static final String SECKILL_QUEUE = "seckill_structure.sql.queue";

    /**
     *
     * @return
     * @description 创建Direct交换机模式的消息队列
     */
    @Bean
    public Queue seckillQueue() {
        return new Queue(SECKILL_QUEUE, true);
    }
}
