package com.wrial.community.mapper;

import com.wrial.community.model.Question;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;


@Mapper
public interface QuestionMapper extends tk.mybatis.mapper.common.Mapper<Question> {

    @Select("select * from question where id = #{id}")
    Question selectById(@Param("id") Integer id);


    @Select("select * from question limit #{offset},#{size}")
    List<Question> selectPage(Integer offset, Integer size);

//    limit #{offset},#{size}
    @Select("select * from question where title like CONCAT('%',#{search},'%') ")
    List<Question> selectByLikeSearch(Integer offset, Integer size, String search);

    @Select("select count(1) from question")
    Integer count();

    @Select("select * from question where creator = #{userId} limit #{offset},#{size}")
    List<Question> listByUser(Long userId, Integer offset, Integer size);

    @Select("select count(1) from question where creator = #{id}")
    Integer countById(Long id);

    @Update("update question set title = #{title},description = #{description},gmt_modified = #{gmtModified},tag = #{tag} where id = #{id}")
    int update(Question question);

    @Update("update question set view_count = view_count+1 where id = #{id}")
    void autoIncView(Integer id);

    @Update("update question set comment_count = comment_count + #{commentCount} where id = #{id}")
    void incCommentCount(Question question);

    @Select("select * from question where tag regexp #{regexpTag} and id!=#{id}")
    List<Question> regexpTags(String regexpTag,Long id);

    @Select("select * from question order by view_count desc limit 0,6")
    List<Question> getHotQuestions();

}
