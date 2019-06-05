package com.wrial.community.controller;

import com.wrial.community.mapper.QuestionMapper;
import com.wrial.community.mapper.UserMapper;
import com.wrial.community.model.Question;
import com.wrial.community.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.List;


@Controller
public class IndexController {


    @Autowired
    private UserMapper userMapper;

    @Autowired
    private QuestionMapper questionMapper;

    //在进入index的时候从数据库中取token检查是否存在当前对象
    @GetMapping("/")
    public String index(HttpServletRequest request,
                        Model model) {


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

        List<Question> questionList = questionMapper.selectAll();
        model.addAttribute("questions",questionList);

        return "index";
    }


}
