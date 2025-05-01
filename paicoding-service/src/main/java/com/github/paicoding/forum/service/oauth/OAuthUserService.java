package com.github.paicoding.forum.service.oauth;

import com.github.paicoding.forum.service.oauth.model.GitHubUserInfo;
import com.github.paicoding.forum.service.user.repository.entity.UserDO;

public interface OAuthUserService {
    /**
     * 根据GitHub用户信息获取或创建用户
     */
    UserDO getOrCreateUser(GitHubUserInfo gitHubUserInfo);

    /**
     * 根据用户名获取用户
     */
    UserDO getUserByUsername(String username);

    /**
     * 创建新用户
     */
    UserDO createUser(GitHubUserInfo gitHubUserInfo);

    void updateUserInfo(UserDO user, GitHubUserInfo info);

    UserDO getUserById(Long id);
}