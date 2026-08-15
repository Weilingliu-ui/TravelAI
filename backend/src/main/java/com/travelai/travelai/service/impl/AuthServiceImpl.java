package com.travelai.travelai.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.travelai.travelai.common.exception.BusinessException;
import com.travelai.travelai.common.response.ResultCode;
import com.travelai.travelai.dto.LoginRequest;
import com.travelai.travelai.dto.LoginResponse;
import com.travelai.travelai.dto.RegisterRequest;
import com.travelai.travelai.entity.User;
import com.travelai.travelai.mapper.UserMapper;
import com.travelai.travelai.security.JwtTokenProvider;
import com.travelai.travelai.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

/**
 * 认证服务实现
 *
 * @author TravelAI Team
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public LoginResponse register(RegisterRequest request) {
        // 检查用户名是否已存在
        Long count = userMapper.selectCount(
                new LambdaQueryWrapper<User>()
                        .eq(User::getUsername, request.getUsername())
        );
        if (count > 0) {
            throw new BusinessException(ResultCode.DATA_DUPLICATE, "用户名已存在");
        }

        // 检查邮箱是否已存在
        if (StrUtil.isNotBlank(request.getEmail())) {
            count = userMapper.selectCount(
                    new LambdaQueryWrapper<User>()
                            .eq(User::getEmail, request.getEmail())
            );
            if (count > 0) {
                throw new BusinessException(ResultCode.DATA_DUPLICATE, "邮箱已被注册");
            }
        }

        // 创建用户
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setRole("USER");
        user.setStatus(1);
        userMapper.insert(user);

        // 生成JWT Token
        String token = jwtTokenProvider.generateToken(user.getId(), user.getUsername());

        return new LoginResponse(token, user.getId(), user.getUsername());
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        // 查询用户
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>()
                        .eq(User::getUsername, request.getUsername())
        );
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND, "用户名或密码错误");
        }

        // 检查用户状态
        if (user.getStatus() == 0) {
            throw new BusinessException(ResultCode.USER_DISABLED);
        }

        // 校验密码
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BusinessException(ResultCode.PASSWORD_ERROR, "用户名或密码错误");
        }

        // 生成JWT Token
        String token = jwtTokenProvider.generateToken(user.getId(), user.getUsername());

        return new LoginResponse(token, user.getId(), user.getUsername());
    }
}
