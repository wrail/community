package com.wrial.community.interceptor;

import com.wrial.community.mapper.UserMapper;
import com.wrial.community.model.Notification;
import com.wrial.community.model.User;
import com.wrial.community.service.NotificationService;
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

    @Autowired
    private NotificationService notificationService;

    /*
    怎么保证每次在页面上的未读通知个数要有，就要放在拦截器里
     */
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
                    if (user!=null) {
                        int unreadCount = notificationService.unRead(user.getId());
                        request.getSession().setAttribute("unreadCount", unreadCount);
                    }
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
