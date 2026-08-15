package com.travelai.travelai.service;

import com.travelai.travelai.dto.LoginRequest;
import com.travelai.travelai.dto.LoginResponse;
import com.travelai.travelai.dto.RegisterRequest;

/**
 * 认证服务接口
 *
 * @author TravelAI Team
 */
public interface AuthService {

    /** 用户注册 */
    LoginResponse register(RegisterRequest request);

    /** 用户登录 */
    LoginResponse login(LoginRequest request);
}
