package com.mate.cloud.googlecheck.service;

import com.mate.cloud.googlecheck.config.GoogleAuthenticatorProperties;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoogleAuthenticatorService {


    private final GoogleAuthenticatorProperties googleAuthenticatorProperties;


    private final GoogleAuthenticator AUTH = new GoogleAuthenticator();


    /**
     * 创建一个新的密钥
     */
    public String createSecretKey() {
        GoogleAuthenticatorKey key = AUTH.createCredentials();
        return key.getKey();
    }

    /**
     * 生成新的密钥和二维码数据
     */
    public GoogleAuthInfo generateSecret(String issuer, String userName) {
        GoogleAuthenticatorKey secretKey = AUTH.createCredentials();
        String secret = secretKey.getKey();
        // 生成 otpauth URI
//        String qrCodeData = GoogleAuthenticatorQRGenerator.getOtpAuthURL(issuer, userName, secretKey);
        String qrCodeData = GoogleAuthenticatorQRGenerator.getOtpAuthURL(issuer, userName, new GoogleAuthenticatorKey.Builder(secret).build());
        return new GoogleAuthInfo(secret, qrCodeData);
    }

    /**
     * 生成二维码URL (APP扫码绑定)
     *
     * @param issuer   应用名称
     * @param userName 用户名
     * @param secret   秘钥
     * @return
     */
    public String getOtpAuthUrl(String issuer, String userName, String secretKey) {
        return GoogleAuthenticatorQRGenerator.getOtpAuthURL(issuer, userName, new GoogleAuthenticatorKey.Builder(secretKey).build());
    }

    // 生成二维码URL（otpauth 协议）
    public String getQrUrl(String username, String secret) {
        return String.format("otpauth://totp/%s?secret=%s&issuer=D7AdminSystem", username, secret);
    }

    /**
     * 带时间窗口的验证（允许前后 windowSize 个步长）
     */
    public boolean verifyCodeWithWindow(String secret, int code) {
        return AUTH.authorize(secret, code, googleAuthenticatorProperties.getWindowSize());
    }


    /**
     * 验证 OTP 是否正确（严格时间窗口）
     */
    public boolean checkCode(String secret, int code) {
        return AUTH.authorize(secret, code);
    }

    @Data
    public static class GoogleAuthInfo {
        private String secret;
        private String qrCodeData;

        public GoogleAuthInfo(String secret, String qrCodeData) {
            this.secret = secret;
            this.qrCodeData = qrCodeData;
        }
    }
}
