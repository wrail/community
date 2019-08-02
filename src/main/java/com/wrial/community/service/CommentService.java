package com.wrial.community.service;

import com.wrial.community.Enums.CommentTypeEnum;
import com.wrial.community.Enums.NotificationStatusEnum;
import com.wrial.community.Enums.NotificationTypeEnum;
import com.wrial.community.dto.CommentDTO;
import com.wrial.community.exception.CustomizeErrorCode;
import com.wrial.community.exception.CustomizeException;
import com.wrial.community.mapper.CommentMapper;
import com.wrial.community.mapper.NotificationMapper;
import com.wrial.community.mapper.QuestionMapper;
import com.wrial.community.mapper.UserMapper;
import com.wrial.community.model.Comment;
import com.wrial.community.model.Notification;
import com.wrial.community.model.Question;
import com.wrial.community.model.User;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.*;
import java.util.stream.Collectors;

/*
在消息通知可以通过insert中进行设置评论的是问题还是评论
 */
@Service
public class CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private NotificationMapper notificationMapper;

    //插入一条评论，但是评论分为一级评论和二级评论,并且评论需要多表完成因此要加上事务，防止数据库抖动
    //Commentator-->评论创建者
    //在Comment中有评论者这个选项为什么还要传一个USER进来，为了减少数据库访问次数，也就相当于是使用缓存
    @Transactional
    public void insert(Comment comment, User commentator) {

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

            //增加回复通知
            Long receiver = dbComment.getCommentator();

            //虽然是回复的评论但是通知还是建立在问题上的
            // 疑问？评论的父ID的父ID一定是问题？？？  因为是二级评论
            Question question = questionMapper.selectById(dbComment.getParentId().intValue());
            if (question == null) {
                throw new CustomizeException(CustomizeErrorCode.TYPE_SET_NONE_OR_WRONG);
            }
            createNotify(comment, receiver, commentator.getName(), question.getTitle(), NotificationTypeEnum.REPLY_COMMENT,question.getId());

        } else {
            //回复问题,就要更新问题表的评论数
            Question question = questionMapper.selectById(new Long(comment.getParentId()).intValue());
            if (question == null) {
                throw new CustomizeException(CustomizeErrorCode.TYPE_SET_NONE_OR_WRONG);
            }
            commentMapper.insert(comment);
            question.setCommentCount(1);
            questionMapper.incCommentCount(question);
            //增加回复通知
            createNotify(comment,question.getCreator(),commentator.getName(), question.getTitle(),NotificationTypeEnum.REPLY_QUESTION,question.getId());
        }
    }

    private void createNotify(Comment comment, Long receiver, String notifierName, String outerTitle, NotificationTypeEnum notificationType,Long outerId) {
        //增加消息通知
        Notification notification = new Notification();
        notification.setGmtCreate(System.currentTimeMillis());
        //最外层问题的ID
        notification.setOuterid(outerId);
        notification.setOuterTitle(outerTitle);
        //通知者
        notification.setNotifier(comment.getCommentator());
        notification.setNotifierName(notifierName);
        //被通知的人
        notification.setReceiver(receiver);
        notification.setStatus(NotificationStatusEnum.UNREAD.getStatus());
        notification.setType(notificationType.getType());

        notificationMapper.insert(notification);
    }

    public List<CommentDTO> listByTargetId(long id, Integer type) {

        Example example = new Example(Comment.class);
        example.createCriteria()
                .andEqualTo("parentId", id)
                .andEqualTo("type", type);
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
