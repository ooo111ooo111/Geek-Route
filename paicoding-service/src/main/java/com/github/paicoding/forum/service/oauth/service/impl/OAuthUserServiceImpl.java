package com.github.paicoding.forum.service.oauth.service.impl;

import com.github.paicoding.forum.service.oauth.model.GitHubUserInfo;
import com.github.paicoding.forum.service.user.repository.entity.UserDO;
import com.github.paicoding.forum.service.oauth.service.OAuthUserService;
import com.github.paicoding.forum.service.user.repository.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OAuthUserServiceImpl implements OAuthUserService {
    @Autowired
    private UserDao userDao;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserDO getOrCreateUser(GitHubUserInfo gitHubUserInfo) {
        // 根据GitHub ID查找用户
        UserDO user = userDao.getByThirdAccountId(gitHubUserInfo.getId().toString());
        if (user != null) {
            // 更新用户信息
            updateUserInfo(user, gitHubUserInfo);
            return user;
        }
        // 创建新用户
        return createUser(gitHubUserInfo);
    }

    @Override
    public UserDO getUserByUsername(String username) {
        return userDao.getUserByUserName(username);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserDO createUser(GitHubUserInfo gitHubUserInfo) {
        UserDO user = new UserDO();
        user.setThirdAccountId(gitHubUserInfo.getId().toString());
        user.setLoginType(1); // GitHub登录
        user.setDeleted(0);
        user.setUserName(gitHubUserInfo.getLogin());
        user.setPassword(""); // GitHub登录不需要密码

        // 保存用户到数据库
        userDao.saveUser(user);
        return user;
    }

    public void updateUserInfo(UserDO user, GitHubUserInfo gitHubUserInfo) {
        user.setUserName(gitHubUserInfo.getLogin());
        userDao.updateUser(user);
    }

    @Override
    public UserDO getUserById(Long id) {
        return userDao.getUserById(id);
    }
}