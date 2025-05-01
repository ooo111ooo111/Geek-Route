package com.github.paicoding.forum.service.session;

import com.github.paicoding.forum.service.session.model.UserSessionDTO;
import com.github.paicoding.forum.service.user.repository.entity.UserDO;

import javax.servlet.http.HttpSession;

/**
 * 会话管理服务接口
 */
public interface SessionService {
    /**
     * 设置用户会话
     * @param session HTTP会话
     * @param user 用户会话DTO
     */
    void setUser(HttpSession session, UserSessionDTO user);

    /**
     * 获取用户会话
     *
     * @param session HTTP会话
     * @return 用户会话DTO
     */
    Object getUser(HttpSession session);

    /**
     * 移除用户会话
     * @param session HTTP会话
     */
    void removeUser(HttpSession session);

    /**
     * 将UserDO转换为UserSessionDTO
     * @param user 用户数据对象
     * @return 用户会话DTO
     */
    UserSessionDTO convertToSessionDTO(UserDO user);

    /**
     * 将UserSessionDTO转换为UserDO
     * @param dto 用户会话DTO
     * @return 用户数据对象
     */
    UserDO convertToUserDO(UserSessionDTO dto);
} 