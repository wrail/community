package com.wrial.community.controller;

import com.mysql.cj.util.StringUtils;
import com.wrial.community.dto.CommentDTO;
import com.wrial.community.dto.ResultDTO;
import com.wrial.community.exception.CustomizeErrorCode;
import com.wrial.community.model.Comment;
import com.wrial.community.model.User;
import com.wrial.community.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
public class CommentController {


    @Autowired
    private CommentService commentService;

    @PostMapping("/comment")
    @ResponseBody
    public Object post(@RequestBody CommentDTO commentDTO,
                       HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            return ResultDTO.errorOf(CustomizeErrorCode.NOT_LOGIN);
        }
        if (commentDTO == null || StringUtils.isNullOrEmpty(commentDTO.getContent())) {
            return ResultDTO.errorOf(CustomizeErrorCode.CONTENT_IS_EMPTY);
        }
        Comment comment = new Comment();
        comment.setCommentator(user.getId());
        comment.setContent(commentDTO.getContent());
        comment.setGmtCreate(System.currentTimeMillis());
        comment.setGmtModified(System.currentTimeMillis());
        comment.setLikeCount(0L);
        comment.setParentId(commentDTO.getParentId());
        comment.setType(commentDTO.getType());

        commentService.insert(comment);

        return ResultDTO.okOf();

    }
}
