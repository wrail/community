package com.wrial.community.mapper;


import com.wrial.community.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;


@Mapper
public interface UserMapper extends tk.mybatis.mapper.common.Mapper<User> {

    @Select("select * from user where token = #{token}")
    User selectByToken(@Param("token") String token);

    @Select("select * from user where id = #{id}")
    User selectById(@Param("id") Long id);

    //得到AccountId
    @Select("select token from user where account_Id = #{accountId}")
    String selectByAccount(String accountId);



}
