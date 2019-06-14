package com.wrial.community.dto;

import com.wrial.community.exception.CustomizeErrorCode;
import com.wrial.community.exception.CustomizeException;
import com.wrial.community.exception.ICustomizeErrorCode;
import lombok.Data;

//基于JSON的格式规定
@Data
public class ResultDTO<T> {

    private Integer code;
    private String message;
    private T data;

    //普通传递参数
    public static ResultDTO errorOf(Integer code, String message) {
        ResultDTO resultDTO = new ResultDTO();
        resultDTO.setCode(code);
        resultDTO.setMessage(message);
        return resultDTO;
    }

    //可以使用枚举进行参数传递
    public static ResultDTO errorOf(ICustomizeErrorCode errorCode) {
        ResultDTO resultDTO = errorOf(errorCode.getCode(), errorCode.getMessage());
        return resultDTO;
    }

    //请求成功
    public static ResultDTO okOf() {
        ResultDTO resultDTO = new ResultDTO();
        resultDTO.setCode(200);
        resultDTO.setMessage("请求成功");
        return resultDTO;

    }
    //请求成功，并返回请求数据（可以是任何类型）
    public static <T> ResultDTO okOf(T t) {
        ResultDTO resultDTO = new ResultDTO();
        resultDTO.setCode(200);
        resultDTO.setMessage("请求成功");
        resultDTO.setData(t);
        return resultDTO;

    }

    public static ResultDTO errorOf(CustomizeException ex) {
        ResultDTO resultDTO = errorOf(ex.getCode(), ex.getMessage());
        return resultDTO;
    }
}
