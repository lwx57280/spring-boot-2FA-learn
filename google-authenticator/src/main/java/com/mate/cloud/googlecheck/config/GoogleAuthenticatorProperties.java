package com.mate.cloud.googlecheck.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;


@Data
@ConfigurationProperties(prefix = "application.google")
public class GoogleAuthenticatorProperties {

    //是否启用
    private boolean enabled;


    //注册路径
    private String register;


    //登录路径
    private String login;


    //验证code路径
    private String verify;

    private int windowSize;
}