package com.wrial.community.dto;

import com.wrial.community.model.User;
import lombok.Data;

@Data
public class QuestionDTO {

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

    //问题的创建者
    private User user;


}
