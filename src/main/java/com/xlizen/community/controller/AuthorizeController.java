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

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

@Controller
public class AuthorizeController {

    @Autowired
    GithubProvider githubProvider;

    @Value("${github.client.id}")
    private String clientId;

    @Value("${github.client.secret}")
    private String clientSecret;

    @Value("${github.redirect.uri}")
    private String redirectUri;

    @Autowired
    UserMapper userMapper;

    @GetMapping("/callback")
    public String callback(@RequestParam(name = "code")String code,
                           @RequestParam(name = "state")String state,
                           HttpServletRequest request){
        AccessTokenDTO tokenDTO = new AccessTokenDTO();
        tokenDTO.setClient_id(clientId);
        tokenDTO.setCode(code);
        tokenDTO.setState(state);
        tokenDTO.setRedirect_uri(redirectUri);
        tokenDTO.setClient_secret(clientSecret);
        String accessToken = githubProvider.getAccessToken(tokenDTO);
        GithubUserDTO githubUser = githubProvider.getGithubUserDTO(accessToken);
        if (githubUser != null) {
            //登录成功，写cookie和session
            User user = new User();
            user.setName(githubUser.getLogin());
            user.setToken(UUID.randomUUID().toString());
            user.setAccountId(String.valueOf(githubUser.getId()));
            user.setGmtCreate(System.currentTimeMillis());
            user.setGmtModified(user.getGmtCreate());
            userMapper.insert(user);
            request.getSession().setAttribute("user",githubUser);
            return "redirect:/";
        } else {
            //重新登陆
            return "redirect:/";
        }

    }
}
