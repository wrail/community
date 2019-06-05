package com.wrial.community.community.mapper;


import com.wrial.community.community.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;


public interface UserMapper extends tk.mybatis.mapper.common.Mapper<User> {

    @Select("select * from user where token = #{token}")
    User selectByToken(@Param("token") String token);
}
