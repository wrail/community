package com.wrial.community.exception;

public enum CustomizeErrorCode implements ICustomizeErrorCode {
    QUESTION_NOT_FOUND(2001, "你找的问题已经搬家了"),
    UPDATE_NOT_ALLOWED(2002, "你的更新出现小问题，请检查账户安全"),
    CONTENT_IS_EMPTY(2003, "请求内容为空"),
    HAVE_NO_COMMENTATOR(2004, "没有提交者信息"),
    NOT_LOGIN(2005, "没有登陆"),
    TYPE_SET_NONE_OR_WRONG(2006, "评论类型错误"),
    SYSTEM_ERROR(2007,"服务器冒烟了请稍后再试"),
    COMMENT_NULL(2008,"评论为空，可能是你写的不规范"),
    NO_SUCH_COMMENT(2009,"这个评论跑路了"),
    READ_NOTIFICATION_FIAL(2010,"这个可不是你的消息呦"),
    NOTIFICATION_NOT_FOUND(2011,"为什么找不到这条消息了");

    private String message;
    private Integer code;

    CustomizeErrorCode(Integer code, String message) {
        this.message = message;
        this.code = code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public Integer getCode() {
        return code;
    }
}
