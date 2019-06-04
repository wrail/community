package com.wrial.community.community.controller;


import com.wrial.community.community.dto.AccessTokenDTO;
import com.wrial.community.community.dto.GitHubUser;
import com.wrial.community.community.provider.GitHubProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

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

    //没返回说明CallBack失败???,原来是大小写问题
    @GetMapping("/callback")
    public String callBack(@RequestParam(name = "code") String code,
                           @RequestParam(name = "state") String state) {

        AccessTokenDTO accessTokenDTO = new AccessTokenDTO();
        accessTokenDTO.setClient_id(client_id);
        accessTokenDTO.setClient_secret(client_secret);
        accessTokenDTO.setCode(code);
        accessTokenDTO.setRedirect_uri(redirect_uri);
        accessTokenDTO.setState(state);
        String accessToken = gitHubProvider.getAccessToken(accessTokenDTO);
        GitHubUser user = gitHubProvider.getUser(accessToken);

        log.info("打印出从github拿的user信息{}",user);
        return "index";
    }

}
