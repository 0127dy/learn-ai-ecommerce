package com.ecommerce.common;

import com.ecommerce.config.SecurityConfig;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 当前用户工具类
 * 从请求属性中获取当前登录用户的 ID
 */
@Component
public class CurrentUserUtil {

    /**
     * 获取当前请求中的用户 ID
     * 从 SecurityConfig JWT 过滤器设置的请求属性中提取
     */
    public static Long getCurrentUserId() {
        ServletRequestAttributes attrs =
            (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) {
            return null;
        }
        HttpServletRequest request = attrs.getRequest();
        Object userId = request.getAttribute(SecurityConfig.CURRENT_USER_ID_ATTR);
        return userId != null ? (Long) userId : null;
    }
}
