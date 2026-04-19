//package com.mate.cloud.googlecheck.interceptor;
//
//import com.mate.cloud.googlecheck.config.GoogleAuthenticatorProperties;
//import com.mate.cloud.googlecheck.service.GoogleAuthenticatorService;
//import com.mate.cloud.googlecheck.utils.GoogleAuthUtil;
//import jakarta.servlet.ServletOutputStream;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.servlet.HandlerInterceptor;
//import org.springframework.web.servlet.ModelAndView;
//
//import java.io.IOException;
//import java.nio.charset.StandardCharsets;
//import java.util.Objects;
//
///**
// * 拦截器
// *
// * @author: MI
// * @email: 448341911@qq.com
// * @createTime: 2026/3/28 16:42
// * @updateUser: MI
// * @updateTime: 2026/3/28 16:42
// * @updateRemark: 修改内容
// * @version: 1.0
// */
//@Slf4j
//public class GoogleAuthenticatorInterceptor implements HandlerInterceptor {
//
//
//    @Autowired
//    GoogleAuthenticatorProperties googleAuthenticatorProperties;
//
//
//    @Autowired
//    private GoogleAuthenticatorService googleAuthenticatorService;
//
//
//    @Override
//    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        // ====================== 核心：开关关闭直接放行 ======================
//        if (googleAuthenticatorProperties.isEnabled()) {
//            return true;
//        }
//        String url = request.getRequestURI();
//        // 配置的放行接口直接通过
//        if (Objects.equals(url, googleAuthenticatorProperties.getRegister())
//                || Objects.equals(url, googleAuthenticatorProperties.getLogin())
//                || Objects.equals(url, googleAuthenticatorProperties.getVerify())) {
//            return true;
//        }
//        String token = request.getHeader("token");
//        //todo 检查token有效性和合法性,以及从服务端能否拿到TOTP验证态
//        if (token == null || token.isEmpty()) {
//            responseNoPermission(response, "token不能为空");
//            return false;
//        }
//
//        // 检查 token 是否有效
//        if (!checkTokenValid(token)) {
//            responseNoPermission(response, "token无效或已过期");
//            return false;
//        }
//
//        // 检查是否已完成谷歌验证
//        if (!checkTOTPValid(token)) {
//            responseNoPermission(response, "请完成谷歌验证器验证");
//            return false;
//        }
//        return true;
//    }
//
//
//    @Override
//    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
//        String url = request.getRequestURI();
//        String username = request.getParameter("username");
//        //如果是登录,登录成功后跳转到绑定Authenticator秘钥或者输入code
//        if (Objects.equals(url, googleAuthenticatorProperties.getLogin())) {
//            try (ServletOutputStream stream = response.getOutputStream()) {
//                GoogleAuthUtil.generateQrCodeBase64(username, stream);
//                return;
//            } catch (IOException e) {
//                log.error("发生错误", e);
//            }
//        }
//        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
//    }
//
//    /**
//     * 校验Token（你自己实现：Redis校验）
//     */
//    private boolean checkTokenValid(String token) {
//        // TODO 你自己实现 Redis 校验token
//        return true;
//    }
//
//    /**
//     * 校验是否已完成谷歌验证（从Redis/DB拿用户状态）
//     */
//    private boolean checkTOTPValid(String token) {
//        // TODO 根据token取用户 → 查 google_enable=1
//        return true;
//    }
//    /**
//     * 返回无权限消息
//     */
//    private void responseNoPermission(HttpServletResponse response, String msg) throws Exception {
//        response.setContentType("application/json;charset=utf-8");
//        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//        response.getOutputStream().write(msg.getBytes(StandardCharsets.UTF_8));
//        response.getOutputStream().flush();
//    }
//}
