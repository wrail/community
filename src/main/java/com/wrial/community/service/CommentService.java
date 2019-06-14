package com.wrial.community.service;

import com.wrial.community.Enums.CommentTypeEnum;
import com.wrial.community.dto.CommentDTO;
import com.wrial.community.exception.CustomizeErrorCode;
import com.wrial.community.exception.CustomizeException;
import com.wrial.community.mapper.CommentMapper;
import com.wrial.community.mapper.QuestionMapper;
import com.wrial.community.mapper.UserMapper;
import com.wrial.community.model.Comment;
import com.wrial.community.model.Question;
import com.wrial.community.model.User;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private UserMapper userMapper;

    //插入一条评论，但是评论分为一级评论和二级评论,并且评论需要多表完成因此要加上事务，防止数据库抖动
    @Transactional
    public void insert(Comment comment) {

        if (comment.getCommentator() == null || comment.getCommentator() == 0) {
            throw new CustomizeException(CustomizeErrorCode.HAVE_NO_COMMENTATOR);
        }
        if (comment.getType() == CommentTypeEnum.COMMENT.getType()) {
            //回复评论，增加评论表的评论数

            Comment dbComment = commentMapper.selectById(comment.getParentId());
            if (dbComment == null) {
                throw new CustomizeException(CustomizeErrorCode.NO_SUCH_COMMENT);
            }
            commentMapper.insert(comment);

            // 增加评论数
            Comment parentComment = new Comment();
            parentComment.setId(comment.getParentId());
            parentComment.setCommentCount(1);
            commentMapper.autoIncCommentCount(parentComment);

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

    public List<CommentDTO> listByTargetId(long id, Integer type) {

        Example example = new Example(Comment.class);
         example.createCriteria()
                .andEqualTo("parentId", id)
                .andEqualTo("type",type);
         //让评论倒叙排序，保证看到的是最新的
         example.setOrderByClause("gmt_create desc");
        List<Comment> comments = commentMapper.selectByExample(example);

        //如果评论为空，就返回空的数组
        if (comments.size() == 0) {
            return new ArrayList<>();
        }
        //拿到去重的评论人
        Set<Long> commentators = comments.stream().map(comment -> comment.getCommentator()).collect(Collectors.toSet());
        ArrayList<Object> userIds = new ArrayList<>();
        userIds.addAll(commentators);

        //根据评论人拿出user，并将user根据id作为key封装到map里
        Example example1 = new Example(User.class);
         example.createCriteria().andIn("id", userIds);
        List<User> userList = userMapper.selectByExample(example1);
        Map<Long, User> userMap = userList.stream().collect(Collectors.toMap(user -> user.getId(), user -> user));

        //根据评论人的流对CommentDTO进行封装
        List<CommentDTO> collect = comments.stream().map(comment -> {
            CommentDTO commentDTO = new CommentDTO();
            BeanUtils.copyProperties(comment, commentDTO);
            User user = userMap.get(comment.getCommentator());
            commentDTO.setUser(user);
            return commentDTO;
        }).collect(Collectors.toList());


        return collect;
    }
}
