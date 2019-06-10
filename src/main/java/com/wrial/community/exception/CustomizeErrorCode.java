package com.wrial.community.exception;

public enum CustomizeErrorCode implements ICustomizeErrorCode {
    QUESTION_NOT_FOUND("你找的问题已经搬家了"),
    UPDATE_NOT_ALLOWED("你的更新出现小问题，请检查账户安全");

    private String message;

    CustomizeErrorCode(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
