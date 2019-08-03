package com.wrial.community.dto;
/*
 * @Author  Wrial
 * @Date Created in 14:33 2019/8/3
 * @Description 文件上传的前后沟通的JSON格式
 */

import lombok.Data;

@Data
public class FileDTO {
    private int success;
    private String message;
    private String url;
}
