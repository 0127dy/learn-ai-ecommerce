package com.ecommerce.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI (Swagger) 配置
 * 访问地址: http://localhost:8080/swagger-ui/index.html
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("AI 电商智能客服与推荐系统 API")
                        .description("基于 Spring Boot 3 + MyBatis-Plus + Redis + RabbitMQ 的电商后端，集成 JWT 鉴权、AI 客服与协同过滤推荐")
                        .version("1.0.0")
                        .contact(new Contact().name("星阑"))
                        .license(new License().name("Apache 2.0")));
    }
}
