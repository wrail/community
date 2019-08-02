package com.wrial.community.Enums;
/*
 * @Author  Wrial
 * @Date Created in 11:36 2019/8/2
 * @Description
 */

import lombok.Getter;


@Getter
public enum NotificationStatusEnum {

    UNREAD(0),
    READ(1)
    ;
    private int status;

    NotificationStatusEnum(int status) {
        this.status = status;
    }

}
