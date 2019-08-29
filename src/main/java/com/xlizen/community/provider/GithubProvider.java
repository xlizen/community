package com.xlizen.community.provider;

import com.alibaba.fastjson.JSON;
import com.xlizen.community.dto.AccessTokenDTO;
import com.xlizen.community.dto.GithubUserDTO;
import okhttp3.*;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Component
public class GithubProvider {

    private static final MediaType MEDIATYPE
            = MediaType.get("application/json; charset=utf-8");

    public String getAccessToken(AccessTokenDTO dto){

        OkHttpClient client = new OkHttpClient();
            RequestBody body = RequestBody.create(MEDIATYPE, JSON.toJSONString(dto));
            Request request = new Request.Builder()
                    .url("https://github.com/login/oauth/access_token")
                    .post(body)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                assert response.body() != null;
                String string = response.body().string();
                String accessToken = string.split("&")[0].split("=")[1];
                System.out.println(accessToken);
                return accessToken;
            } catch (IOException e) {
                e.printStackTrace();
            }
        return null;
    }

    public GithubUserDTO getGithubUserDTO (String accessToken){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://api.github.com/user?access_token="+accessToken)
                .build();

        try (Response response = client.newCall(request).execute()) {
            assert response.body() != null;
            String obj = response.body().string();
            return JSON.parseObject(obj, GithubUserDTO.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
