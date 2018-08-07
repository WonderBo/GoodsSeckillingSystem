package com.cqu.wb.dao;

import com.cqu.wb.domain.SeckillGoods;
import com.cqu.wb.vo.GoodsVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * Created by jingquan on 2018/7/31.
 */
@Mapper
public interface GoodsDao {

    @Select("select g.*, sg.seckill_price, sg.stock_count, sg.start_date, sg.end_date " +
            "from seckill_goods sg left join goods g " +
            "on sg.goods_id = g.id")
    public List<GoodsVo> listGoodsVo();

    @Select("select g.*, sg.seckill_price, sg.stock_count, sg.start_date, sg.end_date " +
            "from seckill_goods sg left join goods g " +
            "on sg.goods_id = g.id " +
            "where g.id = #{goodsId}")
    public GoodsVo getGoodsVoByGoodsId(@Param("goodsId") long goodsId);

    // 解决商品卖超问题：设置stock_count > 0条件（更新操作数据库会加锁）
    // 防止用户刷单（一个用户同时秒杀两个商品）：数据库为userId，goodsId添加一个唯一索引
    @Update("update seckill_goods " +
            "set stock_count = stock_count - 1 " +
            "where goods_id = #{goodsId} and stock_count > 0")
    public int reduceStock(SeckillGoods seckillGoods);

    @Update("update seckill_goods " +
            "set stock_count = #{stockCount} " +
            "where goods_id = #{goodsId}")
    public int resetStock(SeckillGoods seckillGoods);
}
