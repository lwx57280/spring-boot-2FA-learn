package com.mate.cloud.googlecheck.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorConfig;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.HmacHashFunction;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

public class GoogleAuthUtil {

    private static final GoogleAuthenticatorConfig CONFIG = new GoogleAuthenticatorConfig.GoogleAuthenticatorConfigBuilder()
            .setHmacHashFunction(HmacHashFunction.HmacSHA1) // Google Authenticator 默认算法
            .setCodeDigits(6)                               // 验证码位数
            .setTimeStepSizeInMillis(30_000)                // 每30秒更新一次验证码
            .setWindowSize(1)                               // 限制为当前时间窗口
            .build();

    private static final GoogleAuthenticator AUTH = new GoogleAuthenticator(CONFIG);

    // 生成新的Secret Key
    @Deprecated
    public static String generateSecretKey() {
        GoogleAuthenticator gAuth = new GoogleAuthenticator();
        final GoogleAuthenticatorKey key = gAuth.createCredentials();
        return key.getKey();
    }

    /**
     * 创建一个新的密钥
     */
    public static String generateSecretTimeKey() {
        GoogleAuthenticatorKey key = AUTH.createCredentials();
        return key.getKey();
    }

    // 生成二维码URL（otpauth 协议）
    public static String getOtpAuthUrl(String username, String secret) {
        return String.format("otpauth://totp/%s?secret=%s&issuer=D7AdminSystem", username, secret);
    }

    // 生成Base64二维码图片
    public static String generateQrCodeBase64(String otpAuthUrl) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(otpAuthUrl, BarcodeFormat.QR_CODE, 250, 250);
            ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(pngOutputStream.toByteArray());
        } catch (WriterException | IOException e) {
            throw new RuntimeException("生成二维码失败", e);
        }
    }

    // 校验TOTP验证码
    @Deprecated
    public static boolean verifyCode(String secret, int code) {
        GoogleAuthenticator gAuth = new GoogleAuthenticator();
        return gAuth.authorize(secret, code);
    }

    /**
     * 验证 OTP 是否正确（严格时间窗口）
     */
    public static boolean verifyWinCode(String secret, int code) {
        return AUTH.authorize(secret, code);
    }
}
