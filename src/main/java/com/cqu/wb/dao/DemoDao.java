package com.cqu.wb.dao;

import com.cqu.wb.domain.Demo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * Created by jingquan on 2018/7/24.
 */
@Mapper
public interface DemoDao {
    @Select("select * from demo where id = #{id}")
    public Demo getDemoById(@Param("id") int id);

    @Insert("insert into demo(id, name) values(#{id}, #{name})")
    public int insertDemo(Demo demo);
}
