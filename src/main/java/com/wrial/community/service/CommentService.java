package com.wrial.community.service;

import com.wrial.community.Enums.CommentTypeEnum;
import com.wrial.community.exception.CustomizeErrorCode;
import com.wrial.community.exception.CustomizeException;
import com.wrial.community.mapper.CommentMapper;
import com.wrial.community.mapper.QuestionMapper;
import com.wrial.community.model.Comment;
import com.wrial.community.model.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private QuestionMapper questionMapper;

    //插入一条评论，但是评论分为一级评论和二级评论,并且评论需要多表完成因此要加上事务，防止数据库抖动
    @Transactional
    public void insert(Comment comment) {

        if (comment.getCommentator() == null || comment.getCommentator() == 0) {
            throw new CustomizeException(CustomizeErrorCode.HAVE_NO_COMMENTATOR);
        }
        if (comment.getType() == null || !CommentTypeEnum.isExist(comment.getType())) {
            //回复评论，增加评论表的评论数
            Comment dbComment = commentMapper.selectByPrimaryKey(comment.getParentId());
            if (dbComment == null) {
                throw new CustomizeException(CustomizeErrorCode.NO_SUCH_COMMENT);
            }
            commentMapper.insert(comment);

            // 增加评论数
            Comment parentComment = new Comment();
            parentComment.setId(comment.getParentId());
            parentComment.setCommentCount(1);
            commentMapper.autoIncCommentCount(comment);

        } else {
            //回复问题,就要更新问题表的评论数
            Question question = questionMapper.selectById(new Long(comment.getParentId()).intValue());
            if (question == null) {
                throw new CustomizeException(CustomizeErrorCode.TYPE_SET_NONE_OR_WRONG);
            }
            commentMapper.insert(comment);
            question.setCommentCount(1);
            questionMapper.incCommentCount(question);
        }
    }
}
