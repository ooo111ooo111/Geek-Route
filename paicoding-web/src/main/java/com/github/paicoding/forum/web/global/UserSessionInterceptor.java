package com.github.paicoding.forum.web.global;

import com.github.paicoding.forum.service.session.SessionService;
import com.github.paicoding.forum.service.session.model.UserSessionDTO;
import com.github.paicoding.forum.service.user.repository.entity.UserDO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Slf4j
@Component
public class UserSessionInterceptor implements HandlerInterceptor {
    @Autowired
    private SessionService sessionService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession(false);
        if (session != null) {
            UserDO user = (UserDO) sessionService.getUser(session);
            if (user != null) {
                request.setAttribute("currentUser", user);
            }
        }
        return true;
    }
} 