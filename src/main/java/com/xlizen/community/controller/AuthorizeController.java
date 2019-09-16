package com.xlizen.community.controller;


import com.xlizen.community.dto.AccessTokenDTO;
import com.xlizen.community.dto.GithubUserDTO;
import com.xlizen.community.mappper.UserMapper;
import com.xlizen.community.model.User;
import com.xlizen.community.provider.GithubProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Controller
public class AuthorizeController {

    @Autowired
    private GithubProvider githubProvider;

    @Value("${github.client.id}")
    private String clientId;

    @Value("${github.client.secret}")
    private String clientSecret;

    @Value("${github.redirect.uri}")
    private String redirectUri;

    @Autowired
    private UserMapper userMapper;

    @GetMapping("/callback")
    public String callback(@RequestParam(name = "code")String code,
                           @RequestParam(name = "state")String state,
                           HttpServletResponse response){
        AccessTokenDTO tokenDTO = new AccessTokenDTO();
        tokenDTO.setClient_id(clientId);
        tokenDTO.setCode(code);
        tokenDTO.setState(state);
        tokenDTO.setRedirect_uri(redirectUri);
        tokenDTO.setClient_secret(clientSecret);
        String accessToken = githubProvider.getAccessToken(tokenDTO);
        GithubUserDTO githubUser = githubProvider.getGithubUserDTO(accessToken);
        if (githubUser != null) {
            User user = new User();
            user.setName(githubUser.getLogin());
            String token = UUID.randomUUID().toString();
            user.setToken(token);
            user.setAccountId(String.valueOf(githubUser.getId()));
            user.setGmtCreate(System.currentTimeMillis());
            user.setGmtModified(user.getGmtCreate());
            user.setAvatarUrl(githubUser.getAvatarUrl());
            userMapper.insert(user);
            //登录成功，写cookie和session
            response.addCookie(new Cookie("token",token));
            return "redirect:/";
        } else {
            //重新登陆
            return "redirect:/";
        }
    }
}
