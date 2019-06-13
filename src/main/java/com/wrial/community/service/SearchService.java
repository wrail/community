package com.wrial.community.service;

import com.wrial.community.dto.PaginationDTO;
import com.wrial.community.dto.QuestionDTO;
import com.wrial.community.mapper.QuestionMapper;
import com.wrial.community.mapper.UserMapper;
import com.wrial.community.model.Question;
import com.wrial.community.model.User;
import org.apache.ibatis.annotations.Select;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SearchService {

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private UserMapper userMapper;

    public PaginationDTO selectByPage(Integer page, Integer size,String search) {

        List<QuestionDTO> questionDTOS = new ArrayList<>();
        PaginationDTO paginationDTO = new PaginationDTO();

        Integer offset = size * (page - 1);

        if (offset < 0) {
            offset = 0;
        }
        List<Question> questions = questionMapper.selectByLikeSearch(offset,size,search);
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

}
