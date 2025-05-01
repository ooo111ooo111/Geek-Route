package com.github.paicoding.forum.service.oauth.task;

import com.github.paicoding.forum.service.oauth.service.GitHubOAuthService;
import com.github.paicoding.forum.service.oauth.model.GitHubUserInfo;
import com.github.paicoding.forum.service.oauth.service.GitHubTokenService;
import com.github.paicoding.forum.service.oauth.OAuthUserService;
import com.github.paicoding.forum.service.user.repository.dao.UserDao;
import com.github.paicoding.forum.service.user.repository.entity.UserDO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class GitHubSyncTask {
    @Autowired
    private UserDao userDao;
    
    @Autowired
    private GitHubOAuthService gitHubOAuthService;
    
    @Autowired
    private GitHubTokenService gitHubTokenService;
    
    @Autowired
    private OAuthUserService oauthUserService;

    @Scheduled(cron = "0 0 2 * * ?") // 每天凌晨2点执行
    public void syncUserInfo() {
        log.info("开始同步GitHub用户信息");
        try {
            List<UserDO> users = userDao.gteGithubUsers();
            for (UserDO user : users) {
                try {
                    String token = gitHubTokenService.getToken(user.getId().toString());
                    if (token != null) {
                        GitHubUserInfo info = gitHubOAuthService.getUserInfo(token);
                        if (info != null) {
                            oauthUserService.updateUserInfo(user, info);
                            log.info("同步用户 {} 的GitHub信息成功", user.getUserName());
                        } else {
                            log.warn("获取用户 {} 的GitHub信息失败", user.getUserName());
                        }
                    } else {
                        log.warn("用户 {} 的GitHub token不存在", user.getUserName());
                    }
                } catch (Exception e) {
                    log.error("同步用户 {} 的GitHub信息失败", user.getUserName(), e);
                }
            }
            log.info("GitHub用户信息同步完成");
        } catch (Exception e) {
            log.error("GitHub用户信息同步任务执行失败", e);
        }
    }
} 