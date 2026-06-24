package com.ecommerce.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ecommerce.entity.User;
import com.ecommerce.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 数据初始化器
 * 应用启动时自动创建默认用户（Admin + 演示用户）
 * 相比 SQL 硬编码，这种方式能确保 BCrypt 密码哈希正确
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // 检查是否已有管理员
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, "admin");
        if (userMapper.selectOne(wrapper) == null) {
            // 创建管理员（密码: admin123）
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole("ADMIN");
            admin.setPhone("13800000000");
            admin.setEmail("admin@ecommerce.com");
            userMapper.insert(admin);
            log.info("✅ 管理员账号已创建（admin / admin123）");
        }

        // 检查演示用户
        LambdaQueryWrapper<User> wrapper2 = new LambdaQueryWrapper<>();
        wrapper2.eq(User::getUsername, "user1");
        if (userMapper.selectOne(wrapper2) == null) {
            User user1 = new User();
            user1.setUsername("user1");
            user1.setPassword(passwordEncoder.encode("user123"));
            user1.setRole("USER");
            user1.setPhone("13800000001");
            user1.setEmail("user1@ecommerce.com");
            userMapper.insert(user1);
            log.info("✅ 演示用户已创建（user1 / user123）");
        }

        LambdaQueryWrapper<User> wrapper3 = new LambdaQueryWrapper<>();
        wrapper3.eq(User::getUsername, "user2");
        if (userMapper.selectOne(wrapper3) == null) {
            User user2 = new User();
            user2.setUsername("user2");
            user2.setPassword(passwordEncoder.encode("user123"));
            user2.setRole("USER");
            user2.setPhone("13800000002");
            user2.setEmail("user2@ecommerce.com");
            userMapper.insert(user2);
            log.info("✅ 演示用户已创建（user2 / user123）");
        }
    }
}
