package com.github.paicoding.forum.service.oauth.model;

import lombok.Data;

@Data
public class GitHubUserInfo {
    private Long id;
    private String login;
    private String name;
    private String email;
    private String avatarUrl;
    private String bio;
    private String location;
    private String blog;
} 