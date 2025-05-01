package com.github.paicoding.forum.service.oauth.service;

import com.alibaba.fastjson.JSON;
import com.github.paicoding.forum.core.config.GitHubOAuthConfig;
import com.github.paicoding.forum.service.oauth.model.GitHubUserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
@Service
public class GitHubOAuthService {
    @Autowired
    private GitHubOAuthConfig gitHubOAuthConfig;
    
    @Autowired
    private RestTemplate restTemplate;

    /**
     * 获取GitHub授权URL
     */
    public String getAuthorizeUrl(String state) {
        return gitHubOAuthConfig.getAuthorizeUrl() + 
               "?client_id=" + gitHubOAuthConfig.getClientId() + 
               "&redirect_uri=" + gitHubOAuthConfig.getRedirectUri() + 
               "&scope=user";
    }

    /**
     * 获取访问令牌
     */
    public String getAccessToken(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(MediaType.parseMediaTypes("application/json"));

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", gitHubOAuthConfig.getClientId());
        params.add("client_secret", gitHubOAuthConfig.getClientSecret());
        params.add("code", code);
        params.add("redirect_uri", gitHubOAuthConfig.getRedirectUri());

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(
                gitHubOAuthConfig.getAccessTokenUrl(), 
                request, 
                String.class
        );

        if (response.getStatusCode() == HttpStatus.OK) {
            String responseBody = response.getBody();
            Map<String, String> map = JSON.parseObject(responseBody, Map.class);
            return map.get("access_token");
        }
        return null;
    }

    /**
     * 获取用户信息
     */
    public GitHubUserInfo getUserInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "token " + accessToken);
        headers.setAccept(MediaType.parseMediaTypes("application/json"));

        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(
                gitHubOAuthConfig.getUserInfoUrl(),
                HttpMethod.GET,
                request,
                String.class
        );

        if (response.getStatusCode() == HttpStatus.OK) {
            return JSON.parseObject(response.getBody(), GitHubUserInfo.class);
        }
        return null;
    }
} 