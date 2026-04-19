package com.mate.cloud.googlecheck.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mate.cloud.protocol.response.BaseResponseCode;
import com.mate.cloud.protocol.response.CommonResponseCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;


/**
 * 自定义用户未登录的处理（未携带token）
 *
 * @author: MI
 * @email: 448341911@qq.com
 * @createTime: 2026/3/29 19:40
 * @updateUser: MI
 * @updateTime: 2026/3/29 19:40
 * @updateRemark: 修改内容
 * @version: 1.0
 */
@Component
public class AuthEntryPointHandler implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/json; charset=utf-8");
        // 未认证统一返回401，区分"未携带Token"和"Token无效"
        BaseResponseCode resp = new CommonResponseCode(401, "未认证：" + (authException.getMessage() != null ? authException.getMessage() : "未携带token"));
        response.getWriter().write(new ObjectMapper().writeValueAsString(resp));

    }
}