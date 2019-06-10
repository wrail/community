package com.wrial.community.exception;

public class CustomizeException extends RuntimeException {

    private String message;
    private Integer code;

    public CustomizeException(ICustomizeErrorCode customizeErrorCode) {
        this.code = customizeErrorCode.getCode();
        this.message = customizeErrorCode.getMessage();
    }

    @Override
    public String getMessage() {
        return message;
    }

    public Integer getCode() {
        return code;
    }
}
