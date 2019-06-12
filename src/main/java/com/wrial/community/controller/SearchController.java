package com.wrial.community.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SearchController {

    //根据主题查找问题
    @GetMapping(value = "/search")
    public String Search() {


        return "";
    }

}
