package com.wrial.community.advice;

import com.alibaba.fastjson.JSON;
import com.wrial.community.dto.ResultDTO;
import com.wrial.community.exception.CustomizeErrorCode;
import com.wrial.community.exception.CustomizeException;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

//就是拆分开来的Controller和Advice,处理思路：如果是Json格式请求就返回JSON，如果是HTML就返回HTML
//问题就是返回的ModelAndView不能返回两种类型（即使返回值设置为Object也不行），那就只能采取使用Response对象将JSon输出了
@ControllerAdvice
public class CustomizeExceptionHandle {

    @ExceptionHandler(Exception.class)
    ModelAndView handle(Throwable ex,
                        Model model,
                        HttpServletRequest request,
                        HttpServletResponse response) {

        String contentType = request.getContentType();
        if (contentType.equals("application/json")) {
            //如果是自定义异常（自己抛出的异常）就用JSON格式返回
            ResultDTO resultDTO;
            if (ex instanceof CustomizeException) {
                resultDTO = ResultDTO.errorOf((CustomizeException) ex);
            } else {
                //否则就是模板异常
                resultDTO = ResultDTO.errorOf(CustomizeErrorCode.SYSTEM_ERROR);
            }
            try {
                response.setContentType("application/json");
                response.setCharacterEncoding("utf-8");
                PrintWriter writer = response.getWriter();
                writer.write(JSON.toJSONString(resultDTO));
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
            //如果不是JSON，就返回页面
        } else {
            if (ex instanceof CustomizeException) {
                model.addAttribute("message", ex.getMessage());
            } else {
                model.addAttribute("message", CustomizeErrorCode.SYSTEM_ERROR.getMessage());
            }
            return new ModelAndView("error");
        }
    }

    private HttpStatus getStatus(HttpServletRequest request) {

        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        if (statusCode == null) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        } else {
            return HttpStatus.valueOf(statusCode);
        }
    }

}
