package com.wrial.community.community.controller;

import com.wrial.community.community.mapper.UserMapper;
import com.wrial.community.community.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;


@Controller
public class IndexController {


    @Autowired
    private UserMapper userMapper;
    //在进入index的时候从数据库中取token检查是否存在当前对象
    @GetMapping("/")
    public String index(HttpServletRequest request) {

        //跳转过来的时候肯定会有一个token，从数据库中查找出所有信息存在cookie里
        Cookie[] cookies = request.getCookies();
        for (int i = 0; i < cookies.length; i++) {
            if (cookies[i].getName().equals("token")){
                String token = cookies[i].getValue();
                User user = userMapper.selectByToken(token);
                //把session的放入工作放在进入index前做
                request.getSession().setAttribute("user",user);
                System.out.println("存入cookie里的user"+user);
                break;
            }
        }

        return "index";
    }


}
