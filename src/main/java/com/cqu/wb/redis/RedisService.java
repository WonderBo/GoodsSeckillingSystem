package com.cqu.wb.redis;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * Created by jingquan on 2018/7/26.
 */
@Service
public class RedisService {
    @Autowired
    private JedisPool jedisPool;

    /**
     *
     * @param key
     * @param clazz
     * @param <T>
     * @return
     * @description 根据key获取单个对象
     */
    public <T> T get(KeyPrefix keyPrefix, String key, Class<T> clazz) {
        // 输入验证
        if(keyPrefix == null || clazz == null) {
                return null;
        }

        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();    // 从连接池获取Jedis对象
            String realKey = keyPrefix.getKeyPrefix() + key;    // 生成真正的key
            String strVal = jedis.get(realKey);
            T resultVal = stringToBean(strVal, clazz);

            return resultVal;
        } finally {
            returnToPool(jedis);
        }
    }

    /**
     *
     * @param key
     * @param value
     * @param <T>
     * @return
     * @description 根据Key／Value设置对象
     */
    public <T> boolean set(KeyPrefix keyPrefix, String key, T value) {
        // 输入验证
        if(keyPrefix == null || value == null) {
            return false;
        }

        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String realKey = keyPrefix.getKeyPrefix() + key;
            int expireSeconds = keyPrefix.getExpireSeconds();
            String strVal = beanTOString(value);

            if(expireSeconds <= 0) {
                jedis.set(realKey, strVal);                     // 在redis中设置永不过期的Key／Value对
            } else {
                jedis.setex(realKey, expireSeconds, strVal);    // 在redis中设置带过期时间的Key／Value对
            }

            return true;
        } finally {
            returnToPool(jedis);
        }
    }

    /**
     *
     * @param keyPrefix
     * @param key
     * @return
     * @description 根据key在Redis中删除对应数据
     */
    public boolean delete(KeyPrefix keyPrefix, String key) {
        // 输入验证
        if(keyPrefix == null) {
            return false;
        }

        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String realKey = keyPrefix.getKeyPrefix() + key;
            long result = jedis.del(realKey);

            return result > 0;
        } finally {
            returnToPool(jedis);
        }
    }

    /**
     *
     * @param keyPrefix
     * @param key
     * @param <T>
     * @return
     * @description 判断Redis中对应的key是否存在
     */
    public <T> boolean exists(KeyPrefix keyPrefix, String key) {
        // 输入验证
        if(keyPrefix == null) {
            return false;
        }

        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String realKey = keyPrefix.getKeyPrefix() + key;

            return jedis.exists(realKey);
        } finally {
            returnToPool(jedis);
        }
    }

    /**
     *
     * @param keyPrefix
     * @param key
     * @param <T>
     * @return
     * @description 将Redis中对应Key的Value值加1
     */
    public <T> Long incr(KeyPrefix keyPrefix, String key) {
        // 输入验证
        if(keyPrefix == null) {
            return new Long(1);
        }

        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String realKey = keyPrefix.getKeyPrefix() + key;

            return jedis.incr(realKey);
        } finally {
            returnToPool(jedis);
        }
    }

    /**
     *
     * @param keyPrefix
     * @param key
     * @param <T>
     * @return 将Redis中对应Key的Value值减1
     */
    public <T> Long decr(KeyPrefix keyPrefix, String key) {
        // 输入验证
        if(keyPrefix == null) {
            return new Long(-1);
        }

        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String realKey = keyPrefix.getKeyPrefix() + key;

            return jedis.decr(realKey);
        } finally {
            returnToPool(jedis);
        }
    }

    /**
     *
     * @param str
     * @param clazz
     * @param <T>
     * @return
     * @description 将String类型转化为对象类型
     */
    public static <T> T stringToBean(String str, Class<T> clazz) {
        // 基础类型及其封装类型判断（参数校验）
        if(clazz == int.class || clazz == Integer.class) {
            return (T)Integer.valueOf(str);
        } else if(clazz == long.class || clazz == Long.class) {
            return (T)Long.valueOf(str);
        } else if(clazz == String.class) {
            return (T)str;
        } else {
            return JSON.toJavaObject(JSON.parseObject(str), clazz);
        }
    }

    /**
     *
     * @param value
     * @param <T>
     * @return
     * @description 将对象类型转化为String类型
     */
    public static <T> String beanTOString(T value) {
        // 基础类型及其封装类型判断（参数校验）
        Class<?> clazz = value.getClass();
        if(clazz == int.class || clazz == Integer.class) {
            return "" + value;
        } else if(clazz == long.class || clazz == Long.class) {
            return "" + value;
        } else if(clazz == String.class) {
            return (String)value;
        } else {
            return JSON.toJSONString(value);
        }
    }

    /**
     *
     * @param jedis
     * @description Redis连接使用完后回收进连接池
     */
    private void returnToPool(Jedis jedis) {
        if(jedis != null) {
            jedis.close();
        }
    }
}
