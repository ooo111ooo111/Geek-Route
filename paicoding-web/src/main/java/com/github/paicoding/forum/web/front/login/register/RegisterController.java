package com.github.paicoding.forum.web.front.login.register;

import com.github.paicoding.forum.api.model.vo.ResVo;
import com.github.paicoding.forum.api.model.vo.constants.StatusEnum;
import com.github.paicoding.forum.api.model.vo.user.UserPwdLoginReq;
import com.github.paicoding.forum.service.user.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

/**
 * 注册控制器
 */
@RestController
@RequestMapping("/register")
public class RegisterController {

    @Autowired
    private LoginService loginService;

    @PostMapping
    public ResVo<Boolean> register(@RequestParam String username,
                                  @RequestParam String password,
                                  @RequestParam String captcha,
                                  HttpSession session) {
        // 验证验证码
        String sessionCaptcha = (String) session.getAttribute("captcha");
        if (sessionCaptcha == null) {
            return ResVo.fail(StatusEnum.UNEXPECT_ERROR, "验证码已过期");
        }
        if (!sessionCaptcha.equalsIgnoreCase(captcha)) {
            return ResVo.fail(StatusEnum.UNEXPECT_ERROR, "验证码错误");
        }
        // 验证成功后清除session中的验证码
        session.removeAttribute("captcha");

        // 创建注册请求对象
        UserPwdLoginReq loginReq = new UserPwdLoginReq();
        loginReq.setUsername(username);
        loginReq.setPassword(password);

        try {
            // 调用注册服务
            String sessionId = loginService.registerByUserPwd(loginReq);
            if (sessionId != null) {
                return ResVo.ok(true);
            } else {
                return ResVo.fail(StatusEnum.UNEXPECT_ERROR, "注册失败");
            }
        } catch (Exception e) {
            return ResVo.fail(StatusEnum.UNEXPECT_ERROR, e.getMessage());
        }
    }
} 