package com.wrial.community.controller;

import com.mysql.cj.util.StringUtils;
import com.wrial.community.Enums.CommentTypeEnum;
import com.wrial.community.dto.CommentCreateDTO;
import com.wrial.community.dto.CommentDTO;
import com.wrial.community.dto.ResultDTO;
import com.wrial.community.exception.CustomizeErrorCode;
import com.wrial.community.model.Comment;
import com.wrial.community.model.User;
import com.wrial.community.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class CommentController {


    @Autowired
    private CommentService commentService;

    @PostMapping("/comment")
    @ResponseBody
    public ResultDTO CreateComment(@RequestBody CommentCreateDTO commentCreateDTO,
                                   HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            return ResultDTO.errorOf(CustomizeErrorCode.NOT_LOGIN);
        }
        if (commentCreateDTO == null || StringUtils.isNullOrEmpty(commentCreateDTO.getContent())) {
            return ResultDTO.errorOf(CustomizeErrorCode.CONTENT_IS_EMPTY);
        }
        Comment comment = new Comment();
        comment.setCommentator(user.getId());
        comment.setContent(commentCreateDTO.getContent());
        comment.setGmtCreate(System.currentTimeMillis());
        comment.setGmtModified(System.currentTimeMillis());
        comment.setLikeCount(0L);
        comment.setParentId(commentCreateDTO.getParentId());
        comment.setType(commentCreateDTO.getType());

        commentService.insert(comment,user);

        return ResultDTO.okOf();

    }

    @ResponseBody
    @RequestMapping(value = "/comment/{id}",method = RequestMethod.GET)
    //获得评论下的评论内容,因为获取评论和创建评论不同，需要返回一个集合，所以我就在ResultDTO中加入泛型参数
    public ResultDTO<List<CommentDTO>> comments(@PathVariable("id") Long id) {

        List<CommentDTO> commentDTOS = commentService.listByTargetId(id, CommentTypeEnum.COMMENT.getType());

        return ResultDTO.okOf(commentDTOS);

    }
}
