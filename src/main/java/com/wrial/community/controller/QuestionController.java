package com.wrial.community.controller;

import com.wrial.community.Enums.CommentTypeEnum;
import com.wrial.community.dto.CommentDTO;
import com.wrial.community.dto.QuestionDTO;
import com.wrial.community.service.CommentService;
import com.wrial.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class QuestionController {


    @Autowired
    QuestionService questionService;

    @Autowired
    CommentService commentService;

    @GetMapping("/question/{id}")
    public String question(@PathVariable(name = "id") Integer id,
                           Model model) {

        List<CommentDTO> comments = commentService.listByTargetId(id, CommentTypeEnum.QUESTION.getType());

        QuestionDTO questionDTO = questionService.getById(id);

        List<QuestionDTO> relateQuestions = questionService.selectRelateQuestion(questionDTO);

        model.addAttribute("question", questionDTO);
        model.addAttribute("comments", comments);
        model.addAttribute("relateQuestions", relateQuestions);
        questionService.autoIncrViewNum(id);


        return "question";

    }

    //删除question
    @DeleteMapping("/question/{id}")
    public String delQuestion(@PathVariable(name = "id") Integer id) {

        questionService.delQuestion(id);

        return "index";
    }


}
