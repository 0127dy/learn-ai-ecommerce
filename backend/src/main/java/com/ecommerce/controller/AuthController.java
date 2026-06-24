package com.ecommerce.controller;

import com.ecommerce.common.Result;
import com.ecommerce.dto.LoginRequest;
import com.ecommerce.dto.RegisterRequest;
import com.ecommerce.entity.User;
import com.ecommerce.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 * 处理用户注册和登录
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    /**
     * 用户注册
     * POST /api/auth/register
     */
    @PostMapping("/register")
    public Result<User> register(@RequestBody RegisterRequest request) {
        try {
            User user = userService.register(request);
            // 清除密码后返回
            user.setPassword(null);
            return Result.success("注册成功", user);
        } catch (RuntimeException e) {
            return Result.badRequest(e.getMessage());
        }
    }

    /**
     * 用户登录
     * POST /api/auth/login
     * 成功返回 JWT Token
     */
    @PostMapping("/login")
    public Result<Object> login(@RequestBody LoginRequest request) {
        try {
            String token = userService.login(request);
            return Result.success("登录成功", java.util.Map.of("token", token));
        } catch (RuntimeException e) {
            return Result.badRequest(e.getMessage());
        }
    }
}
