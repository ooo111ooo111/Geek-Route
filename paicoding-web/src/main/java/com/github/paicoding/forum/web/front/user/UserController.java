package com.github.paicoding.forum.web.front.user;

import com.github.paicoding.forum.service.session.SessionService;
import com.github.paicoding.forum.service.session.model.UserSessionDTO;
import com.github.paicoding.forum.service.user.repository.entity.UserDO;
import com.github.paicoding.forum.service.oauth.service.GitHubTokenService;
import com.github.paicoding.forum.service.oauth.service.OAuthUserService;
import com.github.paicoding.forum.service.user.repository.dao.UserDao;
import com.github.paicoding.forum.core.util.SessionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;

@Slf4j
@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserDao userDao;
    
    @Autowired
    private GitHubTokenService gitHubTokenService;
    
    @Autowired
    private SessionService sessionService;

    @PostMapping("/unbind/github")
    public String unbindGithub(HttpSession session, RedirectAttributes redirectAttributes) {
        try {
            UserDO user = (UserDO) sessionService.getUser(session);
            if (user != null && user.getGithubId() != null) {
                // 移除GitHub关联
                user.setGithubId(null);
                userDao.update(user);
                
                // 删除token
                gitHubTokenService.removeToken(user.getId().toString());
                
                redirectAttributes.addFlashAttribute("success", "GitHub账号解绑成功");
            } else {
                redirectAttributes.addFlashAttribute("error", "未找到GitHub账号关联");
            }
        } catch (Exception e) {
            log.error("解绑GitHub账号失败", e);
            redirectAttributes.addFlashAttribute("error", "解绑GitHub账号失败：" + e.getMessage());
        }
        return "redirect:/user/profile";
    }

} 