package com.mate.cloud.googlecheck.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Component
@ConfigurationProperties(prefix = SecurityIgnoreProperties.PREFIX) // 绑定前缀
public class SecurityIgnoreProperties {

    public final static String PREFIX = "security.ignore";

    /**
     * 是否启用CORS跨域支持
     */
    private Boolean enabled = false;
    /**
     * key: HTTP方法（GET/POST/*），value: 该方法下的免校验接口列表
     */
    private Map<String, List<String>> urls = new HashMap<>();

    /**
     * 工具方法：判断请求是否在白名单中
     *
     * @param method     HTTP方法（如POST）
     * @param requestUri 请求路径（如/user/register）
     * @return true=在白名单，false=不在
     */
    public boolean isIgnore(String method, String requestUri) {
        // 1. 先匹配精确方法（如POST）
        List<String> exactMethodUrls = urls.get(method);
        if (exactMethodUrls != null && exactMethodUrls.contains(requestUri)) {
            return true;
        }
        // 2. 匹配通配方法（*）
        List<String> allMethodUrls = urls.get("*");
        if (allMethodUrls != null && allMethodUrls.contains(requestUri)) {
            return true;
        }
        return false;
    }

}
