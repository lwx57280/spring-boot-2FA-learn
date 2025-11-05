package com.mate.cloud.googlecheck.base;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import com.mate.cloud.googlecheck.common.ApiException;
import com.mate.cloud.googlecheck.common.ApiResultEnum;
import com.mate.cloud.googlecheck.common.CacheEnum;
import com.mate.cloud.googlecheck.entity.User;
import com.mate.cloud.googlecheck.utils.Tools;



/**
 * 基类
 */
public class BaseController {

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    public HttpServletRequest getRequest() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getRequest();
        return request;
    }

    /**
     * 从token 中获取用户信息
     * @return
     */
    protected User getUser(){
        String tokenKey = Tools.getTokenKey(this.getRequest(), CacheEnum.LOGIN);
        User user = (User) redisTemplate.opsForValue().get(tokenKey);
        if(user == null) throw new ApiException(ApiResultEnum.AUTH_LGOIN_NOT_VALID);
        return user;
    }
}
