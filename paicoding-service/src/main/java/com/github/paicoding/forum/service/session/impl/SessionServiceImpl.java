package com.github.paicoding.forum.service.session.impl;

import com.github.paicoding.forum.service.session.SessionService;
import com.github.paicoding.forum.service.session.model.UserSessionDTO;
import com.github.paicoding.forum.service.user.repository.entity.UserDO;
import com.github.paicoding.forum.core.cache.RedisClient;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;

@Service
public class SessionServiceImpl implements SessionService {
    private static final String USER_SESSION_KEY = "user";

    @Override
    public void setUser(HttpSession session, UserSessionDTO user) {
        session.setAttribute(USER_SESSION_KEY, user);
    }

    @Override
    public Object getUser(HttpSession session) {
        return  session.getAttribute(USER_SESSION_KEY);
    }

    @Override
    public void removeUser(HttpSession session) {
        session.removeAttribute(USER_SESSION_KEY);
    }

    @Override
    public UserSessionDTO convertToSessionDTO(UserDO user) {
        if (user == null) {
            return null;
        }
        UserSessionDTO dto = new UserSessionDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUserName());
        dto.setEmail(user.getEmail());
        dto.setAvatar(user.getAvatar());
        dto.setGithubId(user.getGithubId());
        return dto;
    }

    @Override
    public UserDO convertToUserDO(UserSessionDTO dto) {
        if (dto == null) {
            return null;
        }
        UserDO user = new UserDO();
        user.setId(dto.getId());
        user.setUserName(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setAvatar(dto.getAvatar());
        user.setGithubId(dto.getGithubId());
        return user;
    }

    // 添加 RedisClient 的 Bean 定义
    @Bean
    public RedisClient redisClient() {
        return new RedisClient();
    }
}