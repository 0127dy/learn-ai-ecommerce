package com.ecommerce.service;

import com.ecommerce.dto.LoginRequest;
import com.ecommerce.dto.RegisterRequest;
import com.ecommerce.entity.User;

/**
 * 用户服务接口
 */
public interface UserService {

    /**
     * 用户注册
     * @return 注册成功的用户信息（不含密码）
     */
    User register(RegisterRequest request);

    /**
     * 用户登录
     * @return JWT token
     */
    String login(LoginRequest request);

    /**
     * 根据用户名查询用户
     */
    User getByUsername(String username);

    /**
     * 根据 ID 查询用户
     */
    User getById(Long id);
}
