package com.mate.cloud.googlecheck.config;

import com.mate.cloud.common.config.AbstractMessageSourceConfig;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExceptionMessageSourceConfig extends AbstractMessageSourceConfig {

    private final String[] messages = {"classpath:messages/messages"};

    /**
     * 异常提示消息模板文件路径（即当前项目resources目录）
     */
    @Override
    protected String[] getBaseNames() {
        return messages;
    }
}
