package com.wrial.community.dto;
/*
 * @Author  Wrial
 * @Date Created in 15:01 2019/8/2
 * @Description
 */

import lombok.Data;

@Data
public class NotificationDTO {
    private Long id;
    private Long gmtCreate;
    private Integer status;
    private Long notifier;
    private String notifierName;
    private String outerTitle;
    private Long outerid;
    private String typeName;
    private Integer type;
}
