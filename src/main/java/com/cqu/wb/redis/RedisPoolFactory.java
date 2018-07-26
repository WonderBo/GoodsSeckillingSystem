package com.cqu.wb.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Created by jingquan on 2018/7/26.
 */
@Service
public class RedisPoolFactory {

    @Autowired
    private RedisConfig redisConfig;

    /**
     *
     * @return JedisPool
     * @description 根据得到的配置信息生成Redis连接池
     */
    @Bean
    public JedisPool JedisPoolFactory() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(redisConfig.getPoolMaxIdle());
        jedisPoolConfig.setMaxTotal(redisConfig.getPoolMaxTotal());
        jedisPoolConfig.setMaxWaitMillis(redisConfig.getPoolMaxWait() * 1000);

        JedisPool jedisPool = new JedisPool(jedisPoolConfig, redisConfig.getHost(), redisConfig.getPort(),
                            redisConfig.getTimeout() * 1000, redisConfig.getPassword(), 0);

        return jedisPool;
    }
}
