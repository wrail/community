package com.wrial.community.controller;

import com.wrial.community.dto.PaginationDTO;
import com.wrial.community.mapper.UserMapper;
import com.wrial.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class IndexController {


    @Autowired
    private UserMapper userMapper;

    @Autowired
    private QuestionService questionService;

    //在进入index的时候从数据库中取token检查是否存在当前对象
    @GetMapping("/")
    public String index(Model model,
                        @RequestParam(value = "size",defaultValue = "5") Integer size ,
                        @RequestParam(value = "page",defaultValue = "1") Integer page
    ) {

        //在返回首页之前将我们所有问题展示,使用QuestionDTO包装,加上分页功能，进化为PaginationDTO
        PaginationDTO pagination = questionService.selectByPage(page,size);
        model.addAttribute("pagination",pagination);

        return "index";
    }


}
