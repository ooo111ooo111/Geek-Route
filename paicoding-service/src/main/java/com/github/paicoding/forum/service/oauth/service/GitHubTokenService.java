package com.github.paicoding.forum.service.oauth.service;

import com.github.paicoding.forum.core.cache.RedisClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GitHubTokenService {
    @Autowired
    private RedisClient redisClient;
    
    private static final String TOKEN_KEY = "github:token:";
    private static final long TOKEN_EXPIRE = 7 * 24 * 60 * 60; // 7天
    
    public void saveToken(String userId, String token) {
        redisClient.set(TOKEN_KEY + userId, token, TOKEN_EXPIRE);
    }
    
    public String getToken(String userId) {
        return redisClient.get(TOKEN_KEY + userId);
    }
    
    public void removeToken(String userId) {
        redisClient.del(TOKEN_KEY + userId);
    }
} 