package com.wrial.community.model;
/*
 * @Author  Wrial
 * @Date Created in 11:12 2019/8/2
 * @Description Notification通知类
 */

import lombok.Data;

@Data
public class Notification {

    private Long id;

    private Long notifier;

    private Long receiver;

    //最外层的ID也就是问题的ID
    private Long outerid;

    private Integer type;

    private Long gmtCreate;

    private Integer status;

    private String notifierName;

    private String outerTitle;

}
