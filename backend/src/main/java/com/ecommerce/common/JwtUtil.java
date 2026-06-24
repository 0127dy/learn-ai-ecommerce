package com.ecommerce.common;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT 工具类
 * ⭐⭐⭐ 面试重点：
 * 1. JWT 结构：Header（算法、类型）+ Payload（数据）+ Signature（签名）
 * 2. JWT vs Session：JWT 无状态、跨域友好、适合分布式；Session 有状态、可主动失效
 * 3. Token 刷新策略：短时效 Token + Refresh Token 双 Token 机制
 */
@Component
public class JwtUtil {

    /** 密钥（从配置读取，至少 256 位） */
    @Value("${jwt.secret}")
    private String secret;

    /** Token 过期时间（毫秒） */
    @Value("${jwt.expiration}")
    private long expiration;

    /**
     * 生成 JWT Token
     * @param userId   用户 ID
     * @param username 用户名
     * @param role     用户角色
     * @return JWT token 字符串
     */
    public String generateToken(Long userId, String username, String role) {
        SecretKey key = getSigningKey();

        return Jwts.builder()
            .subject(username)                    // 主体：用户名
            .claim("userId", userId)             // 自定义声明：用户 ID
            .claim("role", role)                 // 自定义声明：角色
            .issuedAt(new Date())                 // 签发时间
            .expiration(new Date(System.currentTimeMillis() + expiration)) // 过期时间
            .signWith(key)                        // 签名
            .compact();
    }

    /**
     * 从 Token 中提取用户名
     */
    public String getUsernameFromToken(String token) {
        return parseClaims(token).getSubject();
    }

    /**
     * 从 Token 中提取用户 ID
     */
    public Long getUserIdFromToken(String token) {
        return parseClaims(token).get("userId", Long.class);
    }

    /**
     * 从 Token 中提取角色
     */
    public String getRoleFromToken(String token) {
        return parseClaims(token).get("role", String.class);
    }

    /**
     * 验证 Token 是否有效
     */
    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * 解析 Token 声明
     */
    private Claims parseClaims(String token) {
        SecretKey key = getSigningKey();
        return Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    /**
     * 生成签名密钥
     * 使用 HMAC-SHA256 算法，要求密钥至少 256 位
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) {
            byte[] padded = new byte[32];
            System.arraycopy(keyBytes, 0, padded, 0, Math.min(keyBytes.length, 32));
            keyBytes = padded;
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
