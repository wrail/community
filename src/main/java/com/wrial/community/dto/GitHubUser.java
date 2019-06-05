package com.wrial.community.dto;

import lombok.Data;

@Data
public class GitHubUser {
    private String name;
    private Long id;
    private String bio;
    //GitHub头像地址
    private String avatar_url;

}
