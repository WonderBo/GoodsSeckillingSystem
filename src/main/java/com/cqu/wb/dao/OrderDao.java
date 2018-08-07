package com.cqu.wb.dao;

import com.cqu.wb.domain.Order;
import com.cqu.wb.domain.SeckillOrder;
import org.apache.ibatis.annotations.*;

/**
 * Created by jingquan on 2018/8/1.
 */
@Mapper
public interface OrderDao {

    @Select("select * " +
            "from seckill_order " +
            "where user_id = #{userId} and goods_id = #{goodsId}")
    public SeckillOrder getSeckillOrderByUserIdGoodsId(@Param("userId")long userId, @Param("goodsId")long goodsId);

    /* 表名或字段名设计的过程中出现mysql关键字，需要用``(数字那一行键的最左边的键，而非单引号)包含起来，否则sql语法错误，下面sql语句中的order为关键字 */
    @Insert("insert into `order`(user_id, goods_id, delivery_addr_id, goods_name, goods_count, goods_price, order_channel, status, create_date) " +
            "values(#{userId}, #{goodsId}, #{deliveryAddrId}, #{goodsName}, #{goodsCount}, #{goodsPrice}, #{orderChannel}, #{status}, #{createDate})")
    @SelectKey(keyColumn = "id", keyProperty = "id", resultType = long.class, before = false, statement = "select last_insert_id()")
    public long insertOrder(Order order);

    @Insert("insert into seckill_order(user_id, order_id, goods_id) " +
            "values(#{userId}, #{orderId}, #{goodsId})")
    public int insertSeckillOrder(SeckillOrder seckillOrder);

    @Select("select * " +
            "from `order` " +
            "where id = #{orderId}")
    public Order getOrderById(@Param("orderId")long orderId);

    @Delete("delete from `order`")
    public void deleteOrders();

    @Delete("delete from seckill_order")
    public void deleteSeckillOrders();
}
