package com.xlizen.community.controller;


import com.xlizen.community.dto.AccessTokenDTO;
import com.xlizen.community.dto.GithubUserDTO;
import com.xlizen.community.provider.GithubProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

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

    @GetMapping("/callback")
    public String callback(@RequestParam(name = "code")String code,
                           @RequestParam(name = "state")String state){
        AccessTokenDTO tokenDTO = new AccessTokenDTO();
        tokenDTO.setClient_id(clientId);
        tokenDTO.setCode(code);
        tokenDTO.setState(state);
        tokenDTO.setRedirect_uri(redirectUri);
        tokenDTO.setClient_secret(clientSecret);
        String accessToken = githubProvider.getAccessToken(tokenDTO);
        GithubUserDTO githubUserDTO = githubProvider.getGithubUserDTO(accessToken);
        System.out.println(githubUserDTO.getLogin());
        return "index";
    }
}
