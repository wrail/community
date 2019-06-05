package com.wrial.community.community.controller;

import com.wrial.community.community.mapper.QuestionMapper;
import com.wrial.community.community.mapper.UserMapper;
import com.wrial.community.community.model.Question;
import com.wrial.community.community.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

@Controller
public class PublishController {

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private UserMapper userMapper;

    //如果是get就请求页面，如果是post就发布信息
    @GetMapping("/publish")
    public String publish() {
        return "publish";
    }

    //在发布之前，先要判断是否登陆（根据数据库的token和cookie），并且user不为空
    @PostMapping("/publish")
    public String doPublish(@RequestParam("title") String title,
                            @RequestParam("description") String description,
                            @RequestParam("tag") String tag,
                            HttpServletRequest request,
                            Model model) {
        User user = null;
        Cookie[] cookies = request.getCookies();
        for (int i = 0; i < cookies.length; i++) {
            if (cookies[i].getValue().equals("token")) {
                user = userMapper.selectByToken(cookies[i].getValue());
                break;
            }
        }

        Question question = new Question();
        question.setTag(tag);
        question.setTitle(title);
        question.setDescription(description);
        question.setGmtCreate(System.currentTimeMillis());
        question.setGmtModified(System.currentTimeMillis());
        //问题的发起者
        question.setCreator(user.getId());
        questionMapper.insert(question);
        model.addAttribute(user);

        return "publish";
    }

}
