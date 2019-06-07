package com.wrial.community.mapper;

import com.wrial.community.model.Question;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;


@Mapper
public interface QuestionMapper extends tk.mybatis.mapper.common.Mapper<Question> {

    @Select("select * from question where id = #{id}")
    public List<Question> selectById(@Param("id") Long id);


    @Select("select * from question limit #{offset},#{size}")
    List<Question> selectPage(Integer offset, Integer size);

    @Select("select count(1) from question")
    Integer count();

    @Select("select * from question where creator = #{userId} limit #{offset},#{size}")
    List<Question> listByUser(Long userId, Integer offset, Integer size);

    @Select("select count(1) from question where creator = #{id}")
    Integer countById(Long id);
}