package com.wrial.community.controller;

import com.wrial.community.mapper.UserMapper;
import com.wrial.community.model.Question;
import com.wrial.community.model.User;
import com.wrial.community.service.QuestionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;

@Controller
@Slf4j
public class PublishController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    QuestionService questionService;

    @GetMapping("/publish/{id}")
    public String editPublish(@PathVariable("id") Integer id,Model model) {

        Question question = questionService.selectById(id);
        if (question != null) {
            model.addAttribute("title", question.getTitle());
            model.addAttribute("description", question.getDescription());
            model.addAttribute("tag", question.getTag());
            model.addAttribute("id", id);
        }
        return "publish";
    }

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
                            @RequestParam(value = "id", required = false) Long id,
                            HttpServletRequest request,
                            Model model) {


        model.addAttribute("title", title);
        model.addAttribute("description", description);
        model.addAttribute("tag", tag);

        if (title == null || title == "") {
            model.addAttribute("info", "标题不能为空");
            return "publish";
        }
        if (description == null || description == "") {
            model.addAttribute("info", "问题补充不能为空");
            return "publish";
        }
        if (tag == null || tag == "") {
            model.addAttribute("info", "标签不能为空");
            return "publish";
        }
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            model.addAttribute("info", "用户未登录");
            return "publish";
        }
        Question question = new Question();
        question.setTag(tag);
        question.setTitle(title);
        question.setDescription(description);
        question.setId(id);
        //问题的发起者
        question.setCreator(Long.valueOf(user.getId()));
        //将创建时间和修改时间操作放入到Service，因为创建和修改对时间的操作不同
        questionService.insertOrUpdate(question);

        //发布成功就直接跳到首页
        return "redirect:/";
    }

}
