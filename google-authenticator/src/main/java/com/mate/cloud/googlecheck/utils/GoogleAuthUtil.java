package com.mate.cloud.googlecheck.utils;

import com.warrenstrange.googleauth.*;
import lombok.experimental.UtilityClass;

@UtilityClass
public class GoogleAuthUtil {

    private final GoogleAuthenticatorConfig CONFIG = new GoogleAuthenticatorConfig.GoogleAuthenticatorConfigBuilder().setHmacHashFunction(HmacHashFunction.HmacSHA1) // Google Authenticator 默认算法
            .setCodeDigits(6)                               // 验证码位数
            .setTimeStepSizeInMillis(30_000)                // 每30秒更新一次验证码
            .setWindowSize(1)                               // 限制为当前时间窗口
            .build();

    private static final GoogleAuthenticator AUTH = new GoogleAuthenticator(CONFIG);


    /**
     * 创建一个新的密钥
     */
    public String createSecretKey() {
        GoogleAuthenticatorKey key = AUTH.createCredentials();
        return key.getKey();
    }

    /**
     * 生成二维码URL (APP扫码绑定)
     * @param issuer  应用名称
     * @param userName 用户名
     * @param secret   秘钥
     * @return
     */
    public String getOtpAuthUrl(String issuer,String userName,String secretKey) {
        return GoogleAuthenticatorQRGenerator.getOtpAuthURL(issuer,userName,
                new GoogleAuthenticatorKey.Builder(secretKey).build());
    }
    // 生成二维码URL（otpauth 协议）
    public String getQrUrl(String username, String secret) {
        return String.format("otpauth://totp/%s?secret=%s&issuer=D7AdminSystem", username, secret);
    }



    /**
     * 验证 OTP 是否正确（严格时间窗口）
     */
    public boolean checkCode(String secret, int code) {
        return AUTH.authorize(secret, code);
    }
}
