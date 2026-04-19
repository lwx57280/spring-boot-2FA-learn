package com.mate.cloud.googlecheck.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mate.cloud.core.config.JwtProperties;
import com.mate.cloud.core.util.JwtGeneratorUtil;
import com.mate.cloud.googlecheck.constant.AuthConstants;
import com.mate.cloud.googlecheck.constant.GoogleExceptionCodeEnum;
import com.mate.cloud.googlecheck.entity.User;
import com.mate.cloud.googlecheck.exception.GoogleAuthenticatorException;
import com.mate.cloud.googlecheck.mapper.UserMapper;
import com.mate.cloud.googlecheck.service.UserService;
import com.mate.cloud.googlecheck.utils.GoogleAuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.mate.cloud.googlecheck.constant.AuthConstants.*;


@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {


    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private final RedisTemplate<String, Object> redisTemplate;


    private final JwtProperties jwtProperties;

    // 注册
    public Boolean register(String userName, String password) {

        if (lambdaQuery().eq(User::getUsername, userName).exists()) {
            throw new GoogleAuthenticatorException(GoogleExceptionCodeEnum.USER_EXIST_WARN.getCode());
        }
        User user = new User();
        user.setUsername(userName);
        user.setPassword(passwordEncoder.encode(password)); // 密码加密
        user.setGoogleSecret(GoogleAuthUtil.createSecretKey());
        user.setGoogleEnable(Boolean.FALSE);
        return save(user);
    }

    // 获取二维码
    public String getQr(String userName) {
        User user = getByUsername(userName);
        return GoogleAuthUtil.getQrUrl(userName, user.getGoogleSecret());
    }

    // 开启谷歌验证
    public String enableGoogle(String userName, Integer code) {
        User user = getByUsername(userName);
        boolean check = GoogleAuthUtil.checkCode(user.getGoogleSecret(), code);
        if (!check) return "验证码错误";
        user.setGoogleEnable(Boolean.TRUE);
        updateById(user);
        return "开启成功";
    }

    // 关闭谷歌验证
    public String disableGoogle(String userName) {
        User user = getByUsername(userName);
        user.setGoogleEnable(Boolean.FALSE);
        updateById(user);
        return "关闭成功";
    }

    // 登录（密码 + 谷歌验证码）
    public Map<String, Object> login(String userName, String password, Integer googleCode) {

        if (isBlocked(userName)) {
            throw new GoogleAuthenticatorException(GoogleExceptionCodeEnum.ACCOUNT_BLOCKED_LOCK_WARN.getCode());
        }
        User user = getByUsername(userName);
        if (user == null) {
            this.loginFailed(userName);
            throw new GoogleAuthenticatorException(GoogleExceptionCodeEnum.USER_ERROR.getCode());
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            this.loginFailed(userName);
            throw new GoogleAuthenticatorException(GoogleExceptionCodeEnum.USER_ERROR.getCode());
        }
        // 登录限流 1分钟5次
        String limitKey = String.format(LOGIN_LIMIT, userName);
        if (redisTemplate.hasKey(limitKey)) {
            throw new GoogleAuthenticatorException(GoogleExceptionCodeEnum.LOGIN_ERROR.getCode());
        }
        redisTemplate.opsForValue().set(limitKey, "1", 1, TimeUnit.MINUTES);

        // 未开启谷歌
        if (user.getGoogleEnable()) {
            if (googleCode == null || googleCode < 0) {
                throw new GoogleAuthenticatorException(GoogleExceptionCodeEnum.GOOGLE_CODE_NEED_WARN.getCode());
            }
        }

        boolean check = GoogleAuthUtil.checkCode(user.getGoogleSecret(), googleCode);
        if (!check) {
            this.loginFailed(userName);

            throw new GoogleAuthenticatorException(GoogleExceptionCodeEnum.GOOGLE_CODE_ERROR.getCode());
        }
        // 登录成功，重置尝试次数
        this.resetAttempts(userName);

        Map<String, Object> claims = new HashMap<>();
        claims.put("userName", user.getUsername());
        // 生成token
        String accessToken = JwtGeneratorUtil.generateAccessToken(user.getUsername(), claims, jwtProperties);
        String refreshToken = JwtGeneratorUtil.generateRefreshToken(user.getUsername(), jwtProperties);


        claims.put("accessToken", accessToken);
        claims.put("refreshToken", refreshToken);
        claims.put("tokenType", jwtProperties.getFullPrefix());
        claims.put("expiresIn", JwtGeneratorUtil.getExpireTime(accessToken));
        // 登录成功存入Redis（30分钟）
        String tokenKey = String.format(LOGIN_TOKEN, userName);
        redisTemplate.opsForValue().set(tokenKey, accessToken, 30, TimeUnit.MINUTES);
        return claims;
    }

    private User getByUsername(String username) {
        return lambdaQuery().eq(User::getUsername, username).one();
    }

    public void loginFailed(String username) {
        String key = getAttemptKey(username);
        Integer attempts = (Integer) redisTemplate.opsForValue().get(key);
        if (attempts == null) {
            redisTemplate.opsForValue().set(key, 1, Duration.ofMinutes(BLOCK_DURATION));
        } else {
            redisTemplate.opsForValue().increment(key);
        }
    }

    public boolean isBlocked(String username) {
        String key = getAttemptKey(username);
        Integer attempts = (Integer) redisTemplate.opsForValue().get(key);
        return attempts != null && attempts >= AuthConstants.MAX_ATTEMPTS;
    }

    public void resetAttempts(String userName) {
        redisTemplate.delete(getAttemptKey(userName));
    }

    private String getAttemptKey(String username) {
        return String.format(AuthConstants.LOGIN_ATTEMPT, username);
    }

}
