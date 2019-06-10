package com.wrial.community.interceptor;

import com.wrial.community.mapper.UserMapper;
import com.wrial.community.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//加上容器注解就不用在使用的时候进行实例化了
@Component
public class SessionInterceptor implements HandlerInterceptor {

    @Autowired
    private UserMapper userMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //跳转过来的时候肯定会有一个token，从数据库中查找出所有信息存在cookie里
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (int i = 0; i < cookies.length; i++) {
                if (cookies[i].getName().equals("token")) {
                    String token = cookies[i].getValue();
                    User user = userMapper.selectByToken(token);
                    //把session的放入工作放在进入index前做
                    request.getSession().setAttribute("user", user);
                    System.out.println("存入cookie里的user" + user);
                    break;
                }
            }
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
