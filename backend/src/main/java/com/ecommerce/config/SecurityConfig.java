package com.ecommerce.config;

import com.ecommerce.common.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * Spring Security 配置
 * JWT 无状态 token 认证
 * ⭐⭐⭐ 面试重点：Spring Security 过滤器链 + JWT 工作原理
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtUtil jwtUtil;

    /** 请求属性中存储当前用户 ID 的 key */
    public static final String CURRENT_USER_ID_ATTR = "currentUserId";

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 禁用 CSRF（JWT 无状态，不需要 CSRF 保护）
            .csrf(csrf -> csrf.disable())
            // 无状态会话 —— 每次请求独立验证
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // 接口权限配置（顺序重要：精确匹配优先）
            .authorizeHttpRequests(auth -> auth
                // ===== 公开接口（无需登录） =====
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/health").permitAll()
                // 商品：GET 查询公开，增删改需 ADMIN
                .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/products/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/products/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/products/**").hasRole("ADMIN")
                // 热销推荐公开
                .requestMatchers("/api/recommend/hot").permitAll()
                // 客服聊天公开
                .requestMatchers("/api/ai/chat").permitAll()
                // 商品评价：查看公开，添加需登录
                .requestMatchers(HttpMethod.GET, "/api/reviews/**").permitAll()
                // ===== 需登录接口 =====
                .requestMatchers("/api/cart/**").authenticated()
                .requestMatchers("/api/orders/**").authenticated()
                .requestMatchers("/api/reviews/**").authenticated()
                .requestMatchers("/api/recommend/**").authenticated()
                // 其余所有接口需登录
                .anyRequest().authenticated()
            )
            // 添加 JWT 认证过滤器（在 UsernamePasswordAuthenticationFilter 之前）
            .addFilterBefore(jwtAuthenticationFilter(),
                UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // ⭐ 面试考点：BCrypt 每次 hash 自动加盐，同一密码每次加密结果不同
        return new BCryptPasswordEncoder();
    }

    /**
     * JWT 认证过滤器
     * 从请求头 Authorization: Bearer <token> 中提取并验证 JWT
     * 验证通过后将用户信息写入 SecurityContext 和请求属性
     */
    @Bean
    public OncePerRequestFilter jwtAuthenticationFilter() {
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain filterChain)
                    throws ServletException, IOException {

                String authHeader = request.getHeader("Authorization");

                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    String token = authHeader.substring(7);
                    try {
                        if (jwtUtil.validateToken(token)) {
                            String username = jwtUtil.getUsernameFromToken(token);
                            Long userId = jwtUtil.getUserIdFromToken(token);
                            String role = jwtUtil.getRoleFromToken(token);

                            // 将 userId 写入请求属性，方便 Controller 获取
                            request.setAttribute(CURRENT_USER_ID_ATTR, userId);

                            SimpleGrantedAuthority authority =
                                new SimpleGrantedAuthority("ROLE_" + role);

                            UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(
                                    username, null, Collections.singletonList(authority));

                            org.springframework.security.core.context.SecurityContextHolder
                                .getContext().setAuthentication(authentication);
                        }
                    } catch (Exception e) {
                        org.springframework.security.core.context.SecurityContextHolder
                            .clearContext();
                    }
                }
                filterChain.doFilter(request, response);
            }
        };
    }
}
