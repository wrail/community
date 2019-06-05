package com.wrial.community.community.model;

import lombok.Data;

@Data
public class Question {
    private Long id;
    private String title;
    private Long gmtCreate;
    private Long gmtModified;
    //创建者
    private Long creator;
    private Integer commentCount;
    //浏览数
    private Integer viewCount;
    //点赞数
    private Integer likeCount;
    //标签
    private String tag;

    private String description;

}
