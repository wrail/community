package com.wrial.community.model;

import lombok.Data;

@Data
public class Comment {

    //评论的Id
    private Long id;
    //父评论的Id
    private Long parentId;
    //类别，是一级评论还是二级评论
    private Integer type;
    //评论者
    private Long commentator;

    private Long gmtCreate;

    private Long gmtModified;
    //点赞
    private Long likeCount;
    //内容
    private String content;

    private Integer commentCount;
}
