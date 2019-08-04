package com.wrial.community.controller;

import com.wrial.community.dto.PaginationDTO;
import com.wrial.community.dto.QuestionDTO;
import com.wrial.community.mapper.UserMapper;
import com.wrial.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


@Controller
public class IndexController {


    @Autowired
    private UserMapper userMapper;

    @Autowired
    private QuestionService questionService;

    //在进入index的时候从数据库中取token检查是否存在当前对象
    //对代码进行重构，将search和index合并
    @GetMapping("/")
    public String index(Model model,
                        @RequestParam(value = "size", defaultValue = "6") Integer size,
                        @RequestParam(value = "page", defaultValue = "1") Integer page,
                        @RequestParam(value = "search", required = false) String search) {


        //不能给此处加判断空，因为第一次search的时候，search是有值的，而在分页内切换分页时，search是没有值的
        //因此，必须将此保存起来，直到下一次下一次到“/”首页的时候search才会被刷新为null
        model.addAttribute("search", search);

        //在返回首页之前将我们所有问题展示,使用QuestionDTO包装,加上分页功能，进化为PaginationDTO
        PaginationDTO pagination = questionService.selectByPage(search, page, size);
        model.addAttribute("pagination", pagination);

        //前八的热门问题
        List<QuestionDTO> hotQuestions = questionService.hotQuestion();
        model.addAttribute("hotQuestions", hotQuestions);

        return "index";
    }


}
