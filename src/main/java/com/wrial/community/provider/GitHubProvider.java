package com.wrial.community.provider;


import com.alibaba.fastjson.JSON;
import com.wrial.community.dto.AccessTokenDTO;
import com.wrial.community.dto.GitHubUser;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.stereotype.Component;

import java.io.IOException;


//Provider:提供一些第三方支持
@Component
@Slf4j
public class GitHubProvider {


    //使用OkHttp发送Post请求
    public String getAccessToken(AccessTokenDTO dto) {
        MediaType mediaType = MediaType.get("application/json; charset=utf-8");

        OkHttpClient client = new OkHttpClient();

        //将AccessTokenDTO转为String传过去
        RequestBody body = RequestBody.create(mediaType, JSON.toJSONString(dto));
        Request request = new Request.Builder()
                .url("https://github.com/login/oauth/access_token")
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            String s = response.body().string();

            //  System.out.println("Response:" + s);
            //可以从输出看到返回的信息access_token=857635261a8ca5bad6bec2366c3a26510bcc6200&scope=user&token_type=bearer
            //只需要token的值,并返回
            String token = s.split("&")[0].split("=")[1];
           log.info("打印出token"+token);
            return token;

        } catch (Exception e) {
            e.printStackTrace();
        }
        //如果没有获取到就返回null
        return null;
    }

    public GitHubUser getUser(String accessToken) {

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://api.github.com/user?access_token=" + accessToken)
                .build();
        try {
            Response response = client.newCall(request).execute();
            String s = response.body().string();
            //会自动将Json转换为类对象
            log.info("打印出响应的user信息{}",s);

            //此处有问题
            GitHubUser gitHubUser = JSON.parseObject(s, GitHubUser.class);
            return gitHubUser;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }


}
