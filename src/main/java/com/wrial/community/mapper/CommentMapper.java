package com.wrial.community.mapper;

import com.wrial.community.model.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface CommentMapper extends tk.mybatis.mapper.common.Mapper<Comment> {

    @Update("update comment set comment_count = comment_count + #{commentCount} where id = #{id}")
    void autoIncCommentCount(Comment comment);

    @Select("select * from comment where id = #{parentId}")
    Comment selectById(Long parentId);

}
