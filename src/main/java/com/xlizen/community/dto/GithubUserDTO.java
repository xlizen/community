package com.xlizen.community.dto;

import lombok.Data;

@Data
public class GithubUserDTO {

    private String login;
    private Long id;
    private String name;
    private String bio;
    private String avatarUrl;

}
