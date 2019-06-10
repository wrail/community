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

@Service
public class CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private QuestionMapper questionMapper;

    public void insert(Comment comment) {

        if (comment.getCommentator() == null || comment.getCommentator() == 0) {
            throw new CustomizeException(CustomizeErrorCode.HAVE_NO_COMMENTATOR);
        }
        if (comment.getType() == null || !CommentTypeEnum.isExist(comment.getType())) {
            //回复评论，增加评论表的评论数
            Comment dbComment = commentMapper.selectByPrimaryKey(comment.getParentId());
            if (dbComment == null) {
                throw new CustomizeException(CustomizeErrorCode.TYPE_SET_NONE_OR_WRONG);
            }
            commentMapper.insert(comment);


            // 增加评论数
            Comment parentComment = new Comment();
            parentComment.setId(comment.getParentId());
            parentComment.setCommentCount(1);
            commentMapper.autoIncCommentCount(comment);

        } else {
            //回复问题,就要更新问题表的评论数
            Question question = questionMapper.selectByPrimaryKey(comment.getParentId());
            if (question == null) {
                throw new CustomizeException(CustomizeErrorCode.TYPE_SET_NONE_OR_WRONG);
            }
            commentMapper.insert(comment);
            commentMapper.insert(comment);
            question.setCommentCount(1);
            questionMapper.incCommentCount(question);
        }
    }
}
