package com.mate.cloud.googlecheck.controller;


import com.mate.cloud.googlecheck.utils.GoogleAuthUtil;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    // 模拟数据库用户
    private static final Map<String, String> userPassword = new HashMap<>();
    private static final Map<String, String> userSecret = new HashMap<>();
    private static final boolean ENABLE_2FA = true; // 全局开关，可来自配置中心

    static {
        userPassword.put("admin", "123456");
    }

    // 登录接口
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> req) {
        String username = req.get("username");
        String password = req.get("password");

        if (!password.equals(userPassword.get(username))) {
            return Map.of("code", 401, "msg", "用户名或密码错误");
        }

        // 判断是否启用 2FA
        if (!ENABLE_2FA) {
            return Map.of("code", 0, "msg", "登录成功（未启用2FA）", "token", UUID.randomUUID().toString());
        }

        String secret = userSecret.get(username);
        if (secret == null) {
//            secret = GoogleAuthUtil.generateSecretKey();
            secret = GoogleAuthUtil.generateSecretTimeKey();
            userSecret.put(username, secret);

            String otpAuthUrl = GoogleAuthUtil.getOtpAuthUrl(username, secret);
            String qrCode = GoogleAuthUtil.generateQrCodeBase64(otpAuthUrl);

            return Map.of(
                    "code", 10001,
                    "msg", "首次绑定，请扫码绑定Google验证器",
                    "require2FASetup", true,
                    "secret", secret,
                    "qrCode", qrCode
            );
        }

        // 已绑定，要求输入验证码
        return Map.of(
                "code", 10002,
                "msg", "请输入动态验证码",
                "require2FA", true
        );
    }

    // 二次验证码校验
    @PostMapping("/verify-2fa")
    public Map<String, Object> verify2FA(@RequestBody Map<String, String> req) {
        String username = req.get("username");
        String code = req.get("otpCode");

        String secret = userSecret.get(username);
        if (secret == null) {
            return Map.of("code", 400, "msg", "未绑定2FA");
        }

//        boolean valid = GoogleAuthUtil.verifyCode(secret, Integer.parseInt(code));
        boolean valid = GoogleAuthUtil.verifyWinCode(secret, Integer.parseInt(code));
        if (!valid) {
            return Map.of("code", 401, "msg", "动态验证码错误");
        }

        return Map.of("code", 0, "msg", "2FA 验证成功", "token", UUID.randomUUID().toString());
    }
}
