package com.wrial.community.controller;
/*
 * @Author  Wrial
 * @Date Created in 0:47 2019/8/3
 * @Description 一些关于通知的处理器
 */

import com.wrial.community.Enums.NotificationTypeEnum;
import com.wrial.community.dto.NotificationDTO;
import com.wrial.community.model.User;
import com.wrial.community.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletRequest;


@Controller
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/notification/{id}")
    public String ReadProfileMessage(@PathVariable("id") Long id,
                                     HttpServletRequest request) {

        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            return "redirect:/";
        }
        NotificationDTO notificationDTO = notificationService.read(id, user);

        if (NotificationTypeEnum.REPLY_COMMENT.getType() == notificationDTO.getType()
                || NotificationTypeEnum.REPLY_QUESTION.getType() == notificationDTO.getType()) {
            return "redirect:/question/" + notificationDTO.getOuterid();
        } else {
            return "redirect:/";
        }

    }

}
