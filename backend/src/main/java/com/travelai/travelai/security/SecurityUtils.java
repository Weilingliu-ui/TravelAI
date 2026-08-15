package com.travelai.travelai.security;

import com.travelai.travelai.entity.User;
import com.travelai.travelai.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * 安全工具类
 * <p>
 * 基于 Spring Security SecurityContextHolder 获取当前登录用户信息。
 * 禁止从前端传入 userId，所有数据归属由本工具类从 JWT Token 中提取。
 *
 * @author TravelAI Team
 */
@Component
@RequiredArgsConstructor
public class SecurityUtils {

    private final UserMapper userMapper;

    /**
     * 获取当前登录用户ID
     *
     * @return 当前用户ID
     * @throws IllegalStateException 未登录时抛出
     */
    public Long getCurrentUserId() {
        UsernamePasswordAuthenticationToken authentication =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("用户未登录");
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof Long) {
            return (Long) principal;
        }

        throw new IllegalStateException("无法获取当前用户ID");
    }

    /**
     * 获取当前登录用户完整信息
     *
     * @return 当前用户实体
     * @throws IllegalStateException 未登录或用户不存在时抛出
     */
    public User getCurrentUser() {
        Long userId = getCurrentUserId();
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new IllegalStateException("当前用户不存在: " + userId);
        }
        return user;
    }
}
