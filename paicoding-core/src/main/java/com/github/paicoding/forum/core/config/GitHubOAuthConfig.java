package com.github.paicoding.forum.core.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "github.oauth")
public class GitHubOAuthConfig {
    private String clientId;
    private String clientSecret;
    private String redirectUri;
    private String authorizeUrl = "https://github.com/login/oauth/authorize";
    private String accessTokenUrl = "https://github.com/login/oauth/access_token";
    private String userInfoUrl = "https://api.github.com/user";
} 