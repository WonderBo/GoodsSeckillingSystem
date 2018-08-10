package com.cqu.wb.rabbitmq;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jingquan on 2018/8/5.
 */

/**
 * @description 消息队列Queue与交换机Exchange存在绑定与路由关系
 */
@Configuration
public class RabbitMQConfig {

    // Direct模式
    public static final String DIRECT_QUEUE = "direct_queue";
    // Topic模式
    public static final String TOPIC_EXCHANGE = "topic_exchange";
    public static final String TOPIC_QUEUE_1 = "topic_queue_1";
    public static final String TOPIC_QUEUE_2 = "topic_queue_2";
    // Fanout模式
    public static final String FANOUT_EXCHANGE = "fanout_exchange";
    public static final String FANOUT_QUEUE_1 = "fanout_queue_1";
    public static final String FANOUT_QUEUE_2 = "fanout_queue_2";
    // Header模式
    public static final String HEADERS_EXCHANGE = "headers_exchange";
    public static final String HEADERS_QUEUE = "header_queue";

    /**
     *
     * @return
     * @description Direct模式交换机（Exchange）与消息队列进行绑定【把消息路由到那些binding key与routing key完全匹配的Queue中】
     */
    @Bean
    public Queue directQueue() {
        return new Queue(DIRECT_QUEUE, true);
    }

    /**
     *
     * @return
     * @description Topic模式交换机（Exchange）与消息队列进行绑定【与Direct类型的Exchange相似，只是由完全匹配扩展为支持模糊匹配】
     */
    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(TOPIC_EXCHANGE);
    }
    @Bean
    public Queue topicQueue1() {
        return new Queue(TOPIC_QUEUE_1, true);
    }
    @Bean
    public Binding topicBinding1() {
        return BindingBuilder.bind(topicQueue1()).to(topicExchange()).with("Topic.Routing.Key.1");
    }
    @Bean
    public Queue topicQueue2() {
        return new Queue(TOPIC_QUEUE_2, true);
    }
    // 每个部分用.分开成为一个单词，其中*表示一个单词，#表示任意数量（零个或多个）单词
    @Bean
    public Binding topicBinding2() {
        return BindingBuilder.bind(topicQueue2()).to(topicExchange()).with("Topic.Routing.Key.#");
    }

    /**
     *
     * @return
     * @description Fanout模式交换机（Exchange）与消息队列进行绑定【把所有发送到该Exchange的消息路由到所有与它绑定的Queue中】
     */
    @Bean
    public FanoutExchange fanoutExchange() {
        return new FanoutExchange(FANOUT_EXCHANGE);
    }
    @Bean
    public Queue fanoutQueue1() {
        return new Queue(FANOUT_QUEUE_1, true);
    }
    @Bean
    public Binding fanoutBinding1() {
        return BindingBuilder.bind(fanoutQueue1()).to(fanoutExchange());
    }
    @Bean
    public Queue fanoutQueue2() {
        return new Queue(FANOUT_QUEUE_2, true);
    }
    @Bean
    public Binding fanoutBinding2() {
        return BindingBuilder.bind(fanoutQueue2()).to(fanoutExchange());
    }

    /**
     *
     * @return
     * @description Headers模式交换机（Exchange）与消息队列进行绑定【根据发送的消息内容中的headers属性进行匹配】
     */
    @Bean
    public HeadersExchange headersExchange() {
        return new HeadersExchange(HEADERS_EXCHANGE);
    }
    @Bean
    public Queue headersQueue() {
        return new Queue(HEADERS_QUEUE, true);
    }
    @Bean
    public Binding headersBinding() {
        // 相比Direct交换机，Header交换机的优势是匹配的规则不被限定为字符串(String)
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("FILTER_HEADER_KEY_1", "FILTER_HEADER_VALUE_1");
        map.put("FILTER_HEADER_KEY_2", "FILTER_HEADER_VALUE_2");
        return BindingBuilder.bind(headersQueue()).to(headersExchange()).whereAll(map).match();
    }
}
