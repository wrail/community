package com.wrial.community.service;

import com.wrial.community.dto.PaginationDTO;
import com.wrial.community.dto.QuestionDTO;
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
//            User user = userMapper.selectByPrimaryKey(question.getCreator());
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
        List<Question> questions = questionMapper.listByUser(userId,offset, size);
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
}