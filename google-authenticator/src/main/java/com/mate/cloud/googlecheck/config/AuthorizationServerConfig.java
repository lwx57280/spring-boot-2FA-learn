package com.mate.cloud.googlecheck.config;

import com.mate.cloud.core.util.SpringContextHolder;
import com.mate.cloud.googlecheck.filter.JwtAuthenticationFilter;
import com.mate.cloud.googlecheck.handler.AuthAccessDeniedHandler;
import com.mate.cloud.googlecheck.handler.AuthEntryPointHandler;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * Spring Security 配置类
 *
 * @author: MI
 * @email: 448341911@qq.com
 * @createTime: 2026/3/29 19:38
 * @updateUser: MI
 * @updateTime: 2026/3/29 19:38
 * @updateRemark: 修改内容
 * @version: 1.0
 */
@Slf4j
@Configuration
@EnableWebSecurity
public class AuthorizationServerConfig {

    @Resource
    private AuthAccessDeniedHandler authAccessDeniedHandler;
    @Resource
    private AuthEntryPointHandler authEntryPointHandler;

    @Resource
    private UserDetailsService userDetailsService;


    /**
     * 公共 CORS 配置组件（新增注入，删除原有 MateBootCorsProperties 直接注入）
     */
    @Resource
    private SecurityIgnoreProperties securityIgnoreProperties;

    //滤器链的相关设置
    // 过滤器链的相关设置
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        log.info("securityFilterChain()");

        // 1. 获取AuthenticationManager（提前获取，避免上下文问题）
        // 构建放行规则（从配置文件读取）
        AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry authorizeRegistry = http.authorizeHttpRequests();

        // 遍历配置的白名单，添加permitAll()放行规则
        securityIgnoreProperties.getUrls().forEach((method, urls) -> {
            if ("*".equals(method)) {
                // 通配方法：所有HTTP方法的接口放行
                authorizeRegistry.requestMatchers(urls.toArray(new String[0])).permitAll();
            } else {
                // 精确方法：指定HTTP方法的接口放行
                authorizeRegistry.requestMatchers(HttpMethod.valueOf(method), urls.toArray(new String[0])).permitAll();
            }
        });

        // 其他请求需要认证
        authorizeRegistry.anyRequest().authenticated();

        // 2. 核心配置（修正链式调用顺序）
        http
                // 禁用csrf保护（前后端分离）
                .csrf(AbstractHttpConfigurer::disable)
                // 禁用默认登录/登出页面
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                // 禁用session（JWT无状态）
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 禁用httpBasic
                .httpBasic(AbstractHttpConfigurer::disable)
                // 自定义异常处理器
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .accessDeniedHandler(authAccessDeniedHandler) // 权限不足
                        .authenticationEntryPoint(authEntryPointHandler) // 未登录/无token
                )
                // 配置身份验证提供者
                .authenticationProvider(authenticationProvider());
        AuthenticationManager authenticationManager = SpringContextHolder.getBean("authenticationManager");
        // 添加JWT过滤器（关键：放在UsernamePasswordAuthenticationFilter之前）
        http.addFilterBefore(new JwtAuthenticationFilter(authenticationManager), UsernamePasswordAuthenticationFilter.class);

        // 配置 CORS 跨域资源共享
        if(Boolean.TRUE.equals(securityIgnoreProperties.getEnabled())){
            http.cors(cors -> cors.configurationSource(corsConfigurationSource()));
        }
        return http.build();
    }


    /**
     * 处理身份验证
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder());
        provider.setUserDetailsService(userDetailsService);
        return provider;
    }


    // 配置密码编码器
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 身份验证管理器
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    /**
     * 跨域配置
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}