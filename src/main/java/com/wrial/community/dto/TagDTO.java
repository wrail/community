package com.wrial.community.dto;

import lombok.Data;

import java.util.List;

//定义标签属性：有分类，每个分类下有多个子标签
@Data
public class TagDTO {

    private String categoryName;
    private List<String> tagList;
}
