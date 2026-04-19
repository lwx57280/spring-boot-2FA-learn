package com.mate.cloud.googlecheck.constant;


import lombok.Getter;

@Getter
public enum GoogleExceptionCodeEnum {

    USER_ERROR("10001"),

    LOGIN_ERROR("10002"),
    /**
     * 谷歌验证码错误
     */
    GOOGLE_CODE_ERROR("10003"),
    /**
     * 用户名已存在
     */
    USER_EXIST_WARN("10004"),
    /**
     * 需要输入Google验证码
     */
    GOOGLE_CODE_NEED_WARN("10005"),
    /**
     * 登录失败次数过多，请15分钟后再试
     */
    ACCOUNT_BLOCKED_LOCK_WARN("10006"),

    ;

    private final String code;

    GoogleExceptionCodeEnum(String code) {
        this.code = code;
    }
}
