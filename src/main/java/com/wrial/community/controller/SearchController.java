package com.wrial.community.controller;

import com.wrial.community.dto.PaginationDTO;
import com.wrial.community.dto.QuestionDTO;
import com.wrial.community.mapper.QuestionMapper;
import com.wrial.community.model.Question;
import com.wrial.community.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Controller
public class SearchController {

    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private SearchService searchService;

    //根据主题查找问题
    @GetMapping(value = "/search")
    public String Search(@RequestParam(value = "search") String search,
                         @RequestParam(value = "size",defaultValue = "7") Integer size ,
                         @RequestParam(value = "page",defaultValue = "1") Integer page,
                         Model model) {

//        Example example = new Example(Question.class);
//        example.createCriteria().andLike("title", '%' + search + '%');
//        List<Question> questions = questionMapper.selectByExample(example);
        PaginationDTO paginationDTO = searchService.selectByPage(size, page, search);

        model.addAttribute("pagination", paginationDTO);
        return "index";
    }

}
