package com.github.paicoding.forum.web.front.login.github;

import com.github.paicoding.forum.service.oauth.service.GitHubOAuthService;
import com.github.paicoding.forum.service.oauth.model.GitHubUserInfo;
import com.github.paicoding.forum.service.user.repository.entity.UserDO;
import com.github.paicoding.forum.service.oauth.service.OAuthUserService;
import com.github.paicoding.forum.service.oauth.service.GitHubTokenService;
import com.github.paicoding.forum.service.session.SessionService;
import com.github.paicoding.forum.service.session.model.UserSessionDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.UUID;

@Slf4j
@Controller
@RequestMapping("/oauth")
public class GitHubOAuthController {
    private static final String STATE_PARAM = "state";
    private static final String ERROR_MSG = "errorMsg";
    private static final String REDIRECT_URL = "redirectUrl";
    
    @Autowired
    private GitHubOAuthService gitHubOAuthService;
    
    @Autowired
    private OAuthUserService userService;
    
    @Autowired
    private GitHubTokenService gitHubTokenService;
    
    @Autowired
    private SessionService sessionService;

    @GetMapping("/github")
    public void authorize(HttpServletResponse response, HttpSession session) throws IOException {
        // 生成state参数，用于防止CSRF攻击
        String state = UUID.randomUUID().toString();
        session.setAttribute(STATE_PARAM, state);
        
        // 保存当前页面URL，用于登录后跳转
        String referer = response.getHeader("Referer");
        if (referer != null && !referer.contains("/oauth/")) {
            session.setAttribute(REDIRECT_URL, referer);
        }
        
        String authorizeUrl = gitHubOAuthService.getAuthorizeUrl(state);
        log.info("Redirecting to GitHub authorization URL: {}", authorizeUrl);
        response.sendRedirect(authorizeUrl);
    }

    @GetMapping("/github/callback")
    public String callback(@RequestParam("code") String code,
                         @RequestParam(value = "state", required = false) String state,
                         @RequestParam(value = "error", required = false) String error,
                         HttpSession session,
                         RedirectAttributes redirectAttributes) {
            System.out.println("code:"+code);
            System.out.println("state:"+state);
            System.out.println("error:"+error);
            // 检查是否有错误返回
            if (error != null) {
                log.error("GitHub OAuth error: {}", error);
                redirectAttributes.addFlashAttribute(ERROR_MSG, "GitHub授权失败: " + error);
                return "redirect:/error";
            }

            // 验证state参数
            String savedState = (String) session.getAttribute(STATE_PARAM);
            System.out.println("savedState:"+savedState);
        if (savedState == null) {
            log.warn("No state found in session, possibly session expired");
            // 继续处理，但记录警告
        } else if (state == null) {
            log.warn("No state received from GitHub callback");
            // 继续处理，但记录警告
        } else if (!savedState.equals(state)) {
            log.error("Invalid state parameter. Expected: {}, Actual: {}", savedState, state);
            redirectAttributes.addFlashAttribute(ERROR_MSG, "无效的请求状态");
            return "redirect:/error";
        }

            // 清除state参数
            session.removeAttribute(STATE_PARAM);

            // 获取访问令牌
            String accessToken = gitHubOAuthService.getAccessToken(code);
            if (accessToken == null) {
                log.error("Failed to get access token for code: {}", code);
                redirectAttributes.addFlashAttribute(ERROR_MSG, "获取GitHub访问令牌失败");
                return "redirect:/error";
            }

            // 获取用户信息
            GitHubUserInfo userInfo = gitHubOAuthService.getUserInfo(accessToken);
            if (userInfo == null) {
                log.error("Failed to get user info with access token");
                redirectAttributes.addFlashAttribute(ERROR_MSG, "获取GitHub用户信息失败");
                return "redirect:/error";
            }

            // 检查用户是否已登录
            UserDO currentUser = (UserDO) sessionService.getUser(session);
            if (currentUser != null) {
                log.info("User {} is already logged in, linking GitHub account", currentUser.getUserName());
                // 关联GitHub账号
                UserDO user = userService.getUserById(currentUser.getId());
                user.setGithubId(userInfo.getId().toString());
                userService.updateUserInfo(user, userInfo);
                gitHubTokenService.saveToken(user.getId().toString(), accessToken);
                return "redirect:/user/profile";
            }

            // 获取或创建用户
            UserDO user = userService.getOrCreateUser(userInfo);
            if (user == null) {
                log.error("Failed to create or get user for GitHub user: {}", userInfo.getLogin());
                redirectAttributes.addFlashAttribute(ERROR_MSG, "用户创建失败");
                return "redirect:/error";
            }

            // 保存token
            gitHubTokenService.saveToken(user.getId().toString(), accessToken);

            // 设置用户会话
            UserSessionDTO userSession = sessionService.convertToSessionDTO(user);
            sessionService.setUser(session, userSession);
            log.info("User {} successfully logged in via GitHub", user.getUserName());

            // 重定向到之前的页面或首页
            String redirectUrl = (String) session.getAttribute(REDIRECT_URL);
            if (redirectUrl != null) {
                session.removeAttribute(REDIRECT_URL);
                return "redirect:" + redirectUrl;
            }

            return "redirect:/";

    }
} 