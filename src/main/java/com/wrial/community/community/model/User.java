package com.wrial.community.community.model;

import lombok.Data;

@Data
public class User {

    private Long id;
    private String accountId;
    private String name;
    private String token;
    private Long gmtCreate;
    private Long gmtModified;


}
