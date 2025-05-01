package com.github.paicoding.forum.web.front.login.captcha;

import com.github.paicoding.forum.api.model.vo.ResVo;
import com.github.paicoding.forum.api.model.vo.constants.StatusEnum;
import com.github.paicoding.forum.core.util.CaptchaUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * 验证码控制器
 */
@RestController
@RequestMapping("/captcha")
public class CaptchaController {

    /**
     * 生成验证码图片
     */
    @GetMapping
    public void generateCaptcha(HttpServletResponse response, HttpSession session) throws IOException {
        // 生成验证码
        String captcha = CaptchaUtil.generateCaptcha();
        // 将验证码存入session
        session.setAttribute("captcha", captcha);
        // 设置响应头  告诉浏览器不要缓存
        response.setContentType("image/jpeg");
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        // 输出图片
        CaptchaUtil.outputImage(captcha, response.getOutputStream());
    }

    /**
     * 验证验证码
     */
    @PostMapping("/verify")
    public ResVo<Boolean> verifyCaptcha(@RequestParam String captcha, HttpSession session) {
        //session不就是后端给浏览器传入的正确验证码  然后做校验
        String sessionCaptcha = (String) session.getAttribute("captcha");
        if (sessionCaptcha == null) {
            return ResVo.fail(StatusEnum.UNEXPECT_ERROR, "验证码已过期");
        }
        if (!sessionCaptcha.equalsIgnoreCase(captcha)) {
            return ResVo.fail(StatusEnum.UNEXPECT_ERROR, "验证码错误");
        }
        // 验证成功后清除session中的验证码
        session.removeAttribute("captcha");
        return ResVo.ok(true);
    }
} 