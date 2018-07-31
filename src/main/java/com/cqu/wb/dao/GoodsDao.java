package com.cqu.wb.dao;

import com.cqu.wb.vo.GoodsVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

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
}
