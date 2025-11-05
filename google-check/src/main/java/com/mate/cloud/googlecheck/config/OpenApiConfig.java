package com.mate.cloud.googlecheck.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger3(OpenAPI3) 接口文档配置
 * 兼容 Spring Boot 3.4 + springdoc-openapi-starter-webmvc-ui 2.6.0
 *
 * 访问地址：
 * http://localhost:8080/swagger-ui.html
 * 或：
 * http://localhost:8080/swagger-ui/index.html
 */
@Configuration
public class OpenApiConfig {

    private static final String TOKEN_HEADER = "Authorization";

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI().info(new Info()
                .title("系统接口文档")
                .version("1.0.0")
                .description("Spring Boot 3.4 + Springdoc + Knife4j 示例")
                .contact(new Contact().name("Mate Cloud")));
    }



}
