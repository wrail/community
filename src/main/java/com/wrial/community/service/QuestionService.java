package com.wrial.community.service;

import com.wrial.community.dto.PaginationDTO;
import com.wrial.community.dto.QuestionDTO;
import com.wrial.community.exception.CustomizeErrorCode;
import com.wrial.community.exception.CustomizeException;
import com.wrial.community.mapper.CommentMapper;
import com.wrial.community.mapper.QuestionMapper;
import com.wrial.community.mapper.UserMapper;
import com.wrial.community.model.Comment;
import com.wrial.community.model.Notification;
import com.wrial.community.model.Question;
import com.wrial.community.model.User;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuestionService {


    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private CommentMapper commentMapper;

    //加上分页
    //理一理逻辑，每一个PageDTO里都有一页的记录，并且展示有没有上一页下一页，首页，和尾页
    public PaginationDTO selectByPage(Integer page, Integer size) {

        List<QuestionDTO> questionDTOS = new ArrayList<>();
        PaginationDTO<QuestionDTO> paginationDTO = new PaginationDTO<>();

        Integer offset = size * (page - 1);

        if (offset < 0) {
            offset = 0;
        }

        //后写的排在前面
        Example example = new Example(Question.class);
        example.setOrderByClause("gmt_create desc");
        List<Question> questions = questionMapper.selectByExampleAndRowBounds(example, new RowBounds(offset, size));


//        List<Question> questions = questionMapper.selectPage(offset, size);
//        List<Question> questions = questionMapper.selectAll();
        for (Question question : questions) {
            User user = userMapper.selectById(question.getCreator());
            QuestionDTO dto = new QuestionDTO();
            //使用BeanUtils将question的属性拷贝到QuestionDTO
            BeanUtils.copyProperties(question, dto);
            dto.setUser(user);
            questionDTOS.add(dto);
        }
        paginationDTO.setData(questionDTOS);
        Integer totalCount = questionMapper.count();
        paginationDTO.setPagination(totalCount, page, size);

        return paginationDTO;
    }


    public PaginationDTO list(Long userId, Integer page, Integer size) {
        List<QuestionDTO> questionDTOS = new ArrayList<>();
        PaginationDTO<QuestionDTO> paginationDTO = new PaginationDTO<>();

        Integer offset = size * (page - 1);

        if (offset < 0) {
            offset = 0;
        }
        List<Question> questions = questionMapper.listByUser(userId, offset, size);
        for (Question question : questions) {
            User user = userMapper.selectById(question.getCreator());
            QuestionDTO dto = new QuestionDTO();
            //使用BeanUtils将question的属性拷贝到QuestionDTO
            BeanUtils.copyProperties(question, dto);
            dto.setUser(user);
            questionDTOS.add(dto);
        }
        paginationDTO.setData(questionDTOS);

        Integer totalCount = questionMapper.countById(userId);
        paginationDTO.setPagination(totalCount, page, size);

        return paginationDTO;
    }


    public QuestionDTO getById(Integer id) {

        QuestionDTO questionDTO = new QuestionDTO();
        Question question = questionMapper.selectById(id);
        if (question == null) {
            throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
        }
        BeanUtils.copyProperties(question, questionDTO);
        User user = userMapper.selectById(question.getCreator());
        questionDTO.setUser(user);
        return questionDTO;
    }

    //进行插入或更新操作
    public void insertOrUpdate(Question question) {

        if (question.getId() == null) {
            question.setGmtCreate(System.currentTimeMillis());
            question.setGmtModified(System.currentTimeMillis());
            questionMapper.insert(question);
        } else {
            question.setGmtModified(System.currentTimeMillis());
            int i = questionMapper.update(question);
            if (i != 1) {
                throw new CustomizeException(CustomizeErrorCode.UPDATE_NOT_ALLOWED);
            }
        }

    }

    //根据解析标签查出相关问题,根据拿出的所有Tag并且用'|'联接，使用正则匹配的模糊查询
    public List<QuestionDTO> selectRelateQuestion(QuestionDTO queryDTO) {

        String[] split = queryDTO.getTag().split("，");
        //得到用“|”进行连接的字符串，放到sql中进行正则查询
        String regexpTag = Arrays.asList(split).stream().collect(Collectors.joining("|"));

        //使用正则判断并且要除过自己所以需要传入自己的Id
        List<Question> questionList = questionMapper.regexpTags(regexpTag, queryDTO.getId());

        List<QuestionDTO> collect = questionList.stream().map(q -> {
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(q, questionDTO);
            return questionDTO;
        }).collect(Collectors.toList());

        return collect;
    }

    public Question selectById(Integer id) {
        return questionMapper.selectById(id);
    }

    //自增阅读数,细节：在更新的时候使用view_count=view_count+1，不要使用Example中的比较并+1
    public void autoIncrViewNum(Integer id) {
        questionMapper.autoIncView(id);

    }

    //删除问题
    //解决思路：删除问题，并删除所有问题对应的评论,因此得加上事务，并且涉及到二级评论
    //先删除Question，再删除Comment（一级），根据删除的Comment再删除二级Comment
    @Transactional
    public void delQuestion(Integer id) {

        Example example = new Example(Question.class);
        example.createCriteria().andEqualTo("id", id);
        questionMapper.deleteByExample(example);

        Example example2 = new Example(Comment.class);
        example2.createCriteria().andEqualTo("parentId", id);
        List<Comment> comments = commentMapper.selectByExample(example2);
        commentMapper.deleteByExample(example2);
        if (comments != null) {

            for (Comment comment : comments) {
                Example example1 = new Example(Comment.class);
                example1.createCriteria().andEqualTo("parentId", comment.getId());
                commentMapper.deleteByExample(example1);
            }
        }

    }

    //查找8前八的热门话题
    public List<QuestionDTO> hotQuestion() {

        List<Question> hotQuestions = questionMapper.getHotQuestions();
        List<QuestionDTO> collect = hotQuestions.stream().map(q -> {
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(q, questionDTO);
            User user = userMapper.selectById(q.getCreator());
            questionDTO.setUser(user);
            return questionDTO;
        }).collect(Collectors.toList());

        return collect;
    }

}
