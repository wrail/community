package com.wrial.community.Enums;
/*
 * @Author  Wrial
 * @Date Created in 11:18 2019/8/2
 * @Description 包含通知的类型（比如是评论问题的通知还是点赞通知还是评论评论的通知）
 */

import lombok.Getter;

@Getter
public enum NotificationTypeEnum {

    REPLY_QUESTION(1,"回复了问题"),
    REPLY_COMMENT(2, "回复了评论"),
    THUMBS_UP(3, "点了一个赞"),
    ;
    private int type;
    private String name;

    NotificationTypeEnum(int type, String name) {
        this.type = type;
        this.name = name;
    }

    public static String nameOfType(Integer type) {
        for (NotificationTypeEnum notificationTypeEnum:NotificationTypeEnum.values()){
            if (notificationTypeEnum.type==type){
                return notificationTypeEnum.name;
            }
        }
        return "";
    }
}
