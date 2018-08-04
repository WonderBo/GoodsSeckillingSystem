package com.cqu.wb.dao;

import com.cqu.wb.domain.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * Created by jingquan on 2018/7/29.
 */
@Mapper
public interface UserDao {

    @Select("select * from user where id = #{id}")
    public User getUserById(@Param("id") long id);

    @Update("update user set password = #{password} where id = #{password}")
    public void updateUserPassword(User user);
}
