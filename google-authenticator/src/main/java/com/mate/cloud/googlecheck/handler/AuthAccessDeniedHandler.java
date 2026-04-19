package com.mate.cloud.googlecheck.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.mate.cloud.protocol.response.Responses.successMsgResponse;


/**
 * 自定义用户权限不足的处理
 * @author:         MI
 * @email:          448341911@qq.com
 * @createTime:     2026/3/29 19:40
 * @updateUser:     MI
 * @updateTime:     2026/3/29 19:40
 * @updateRemark:   修改内容
 * @version:        1.0
 */
@Component
public class AuthAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/json; charset=utf-8");
        String value = new ObjectMapper().writeValueAsString(successMsgResponse("权限不足！"));
        response.getWriter().write(value);
    }
}