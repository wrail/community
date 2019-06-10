package com.wrial.community.advice;

import com.wrial.community.exception.CustomizeException;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.HttpServletRequest;

//就是拆分开来的Controller和Advice
@ControllerAdvice
public class CustomizeExceptionHandle {

    @ExceptionHandler(Exception.class)
    ModelAndView handle(Throwable ex,
                        Model model) {

        if (ex instanceof CustomizeException){
            model.addAttribute("message",ex.getMessage());
        }else {
            model.addAttribute("message", "服务器冒烟了");
        }
        return new ModelAndView("error");
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
