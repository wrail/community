package com.wrial.community.service;

import com.wrial.community.dto.PaginationDTO;
import com.wrial.community.dto.QuestionDTO;
import com.wrial.community.exception.CustomizeErrorCode;
import com.wrial.community.exception.CustomizeException;
import com.wrial.community.mapper.QuestionMapper;
import com.wrial.community.mapper.UserMapper;
import com.wrial.community.model.Question;
import com.wrial.community.model.User;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class QuestionService {


    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private UserMapper userMapper;

    //加上分页
    //理一理逻辑，每一个PageDTO里都有一页的记录，并且展示有没有上一页下一页，首页，和尾页
    public PaginationDTO selectByPage(Integer page, Integer size) {

        List<QuestionDTO> questionDTOS = new ArrayList<>();
        PaginationDTO paginationDTO = new PaginationDTO();

        Integer offset = size * (page - 1);

        if (offset < 0) {
            offset = 0;
        }
        List<Question> questions = questionMapper.selectPage(offset, size);
//        List<Question> questions = questionMapper.selectAll();
        for (Question question : questions) {
            User user = userMapper.selectById(question.getCreator());
            QuestionDTO dto = new QuestionDTO();
            //使用BeanUtils将question的属性拷贝到QuestionDTO
            BeanUtils.copyProperties(question, dto);
            dto.setUser(user);
            questionDTOS.add(dto);
        }
        paginationDTO.setQuestions(questionDTOS);
        Integer totalCount = questionMapper.count();
        paginationDTO.setPagination(totalCount, page, size);

        return paginationDTO;
    }


    public PaginationDTO list(Long userId, Integer page, Integer size) {
        List<QuestionDTO> questionDTOS = new ArrayList<>();
        PaginationDTO paginationDTO = new PaginationDTO();

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
        paginationDTO.setQuestions(questionDTOS);

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

    public Question selectById(Integer id) {
        return questionMapper.selectById(id);
    }

    //自增阅读数,细节：在更新的时候使用view_count=view_count+1，不要使用Example中的比较并+1
    public void autoIncrViewNum(Integer id) {
        questionMapper.autoIncView(id);

    }
}
