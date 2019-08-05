package com.wrial.community.controller;
/*
 * @Author  Wrial
 * @Date Created in 11:10 2019/8/5
 * @Description 精品视频页面
 */

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class VideoController {

    @RequestMapping("/videos")
    public String toVideosPage(){

        return "videos";
    }
}
