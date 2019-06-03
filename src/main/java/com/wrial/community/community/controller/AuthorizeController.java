package com.wrial.community.community.controller;


import com.wrial.community.community.dto.AccessTokenDTO;
import com.wrial.community.community.dto.GitHubUser;
import com.wrial.community.community.provider.GitHubProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthorizeController {


    @Autowired
    GitHubProvider gitHubProvider;


    //没返回说明CallBack失败???,原来是大小写问题
    @GetMapping("/callback")
    public String callBack(@RequestParam(name = "code") String code,
                           @RequestParam(name = "state") String state) {

        AccessTokenDTO accessTokenDTO = new AccessTokenDTO();
        accessTokenDTO.setClient_id("70e24ea8682a92b4239f");
        accessTokenDTO.setClient_secret("3c03dc4d0c5b9c6e04f1f5b2546121a27813add2");
        accessTokenDTO.setCode(code);
        accessTokenDTO.setRedirect_uri("http://localhost:8080/callback");
        accessTokenDTO.setState(state);
        String accessToken = gitHubProvider.getAccessToken(accessTokenDTO);
        GitHubUser user = gitHubProvider.getUser(accessToken);
        System.out.println(user.getName());

        return "index";
    }

}
