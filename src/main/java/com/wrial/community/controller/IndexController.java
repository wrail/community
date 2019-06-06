package com.wrial.community.controller;

import com.wrial.community.dto.PaginationDTO;
import com.wrial.community.mapper.UserMapper;
import com.wrial.community.model.User;
import com.wrial.community.service.QuestionService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;


@Controller
public class IndexController {


    @Autowired
    private UserMapper userMapper;

    @Autowired
    private QuestionService questionService;

    //在进入index的时候从数据库中取token检查是否存在当前对象
    @GetMapping("/")
    public String index(HttpServletRequest request,
                        Model model,
                        @RequestParam(value = "size",defaultValue = "5") Integer size ,
                        @RequestParam(value = "page",defaultValue = "1") Integer page
    ) {

        //跳转过来的时候肯定会有一个token，从数据库中查找出所有信息存在cookie里
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (int i = 0; i < cookies.length; i++) {
                if (cookies[i].getName().equals("token")) {
                    String token = cookies[i].getValue();
                    User user = userMapper.selectByToken(token);
                    //把session的放入工作放在进入index前做
                    request.getSession().setAttribute("user", user);
                    System.out.println("存入cookie里的user" + user);
                    break;
                }
            }
        }

        //在返回首页之前将我们所有问题展示,使用QuestionDTO包装,加上分页功能，进化为PaginationDTO
        PaginationDTO pagination = questionService.selectByPage(page,size);
        model.addAttribute("pagination",pagination);

        return "index";
    }


}
