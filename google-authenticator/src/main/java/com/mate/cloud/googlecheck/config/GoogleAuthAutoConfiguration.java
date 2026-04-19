//package com.mate.cloud.googlecheck.config;
//
//import com.mate.cloud.googlecheck.interceptor.GoogleAuthenticatorInterceptor;
//import com.mate.cloud.googlecheck.service.GoogleAuthenticatorService;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
//import org.springframework.boot.context.properties.EnableConfigurationProperties;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.Ordered;
//import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//@Configuration
//@EnableConfigurationProperties(GoogleAuthenticatorProperties.class)
//@ConditionalOnProperty(prefix = "application.google", name = "enabled", havingValue = "true")
//public class GoogleAuthAutoConfiguration {
//
//    @Bean
//    public GoogleAuthenticatorService googleAuthenticatorService() {
//        return new GoogleAuthenticatorService();
//    }
//
//    @Bean
//    public GoogleAuthenticatorInterceptor googleAuthenticatorInterceptor() {
//        return new GoogleAuthenticatorInterceptor();
//    }
//
//    @Configuration
//    public class CustomInterceptorConfig implements WebMvcConfigurer {
//        private GoogleAuthenticatorInterceptor googleAuthenticatorInterceptor;
//
//
//        public CustomInterceptorConfig() {
//            googleAuthenticatorInterceptor = googleAuthenticatorInterceptor();
//        }
//
//        @Override
//        public void addInterceptors(InterceptorRegistry registry) {
//            registry.addInterceptor(googleAuthenticatorInterceptor)
//                    .addPathPatterns("/**") // 可以根据需要指定拦截的路径
//                    .order(Ordered.LOWEST_PRECEDENCE); // 设置执行顺序为最低优先级
//        }
//    }
//}