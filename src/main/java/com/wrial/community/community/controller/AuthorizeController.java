package com.wrial.community.community.controller;


import com.sun.deploy.net.HttpResponse;
import com.wrial.community.community.dto.AccessTokenDTO;
import com.wrial.community.community.dto.GitHubUser;
import com.wrial.community.community.mapper.UserMapper;
import com.wrial.community.community.model.User;
import com.wrial.community.community.provider.GitHubProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Controller
@Slf4j
public class AuthorizeController {


    @Autowired
    GitHubProvider gitHubProvider;

    @Value("${github.client.id}")
    private String client_id;
    @Value("${github.client.secret}")
    private String client_secret;
    @Value("${github.redirect.uri}")
    private String redirect_uri;

    @Autowired
    UserMapper userMapper;

    //没返回说明CallBack失败???,原来是大小写问题
    @GetMapping("/callback")
    public String callBack(@RequestParam(name = "code") String code,
                           @RequestParam(name = "state") String state,
//                           HttpServletRequest request,
                           HttpServletResponse response) {

        AccessTokenDTO accessTokenDTO = new AccessTokenDTO();
        accessTokenDTO.setClient_id(client_id);
        accessTokenDTO.setClient_secret(client_secret);
        accessTokenDTO.setCode(code);
        accessTokenDTO.setRedirect_uri(redirect_uri);
        accessTokenDTO.setState(state);
        String accessToken = gitHubProvider.getAccessToken(accessTokenDTO);
        GitHubUser gitHubUser = gitHubProvider.getUser(accessToken);
        //不管登陆是否成功都要返回到首页,使用重定向
        if (gitHubUser != null) {
            //因为采用了token机制就不需要session了
            //request.getSession().setAttribute("gitHubUser", gitHubUser);
            User user = new User();
            //生成一个随机的UUID存在数据库
            String token = UUID.randomUUID().toString();
            //将token放在Cookie里
            response.addCookie(new Cookie("token", token));
            user.setToken(token);
            user.setName(gitHubUser.getName());
            user.setAccountId(String.valueOf(gitHubUser.getId()));
            user.setGmtCreate(System.currentTimeMillis());
            user.setGmtModified(System.currentTimeMillis());

            userMapper.insert(user);


            log.info("打印出从github拿的user信息{}", gitHubUser);
            //redirect不能直接加页面不然会404，直接到根目录就自动进入首页了
            return "redirect:/";
        } else {
            return "redirect:/";

        }
    }

}
