package com.mate.cloud.googlecheck.filter;

import com.mate.cloud.core.common.constant.TokenConstant;
import com.mate.cloud.core.util.JwtGeneratorUtil;
import com.mate.cloud.core.util.SpringContextHolder;
import com.mate.cloud.core.util.StringUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static com.mate.cloud.core.util.JwtGeneratorUtil.getSubject;


/**
 * 构建JWT验证过滤器
 * 新增：跳过登录/注册接口的token校验
 * @author:         MI
 * @email:          448341911@qq.com
 * @createTime:     2026/3/29 19:37
 * @updateUser:     MI
 * @updateTime:     2026/3/29 19:37
 * @updateRemark:   修改内容
 * @version:        1.0
 */
@Slf4j
@Component
public class JwtAuthenticationFilter extends BasicAuthenticationFilter {

    // 注入UserDetailsService（根据你的实际注入方式调整）
    private final UserDetailsService userDetailsService;


    // 放行的接口列表（无需token校验）
    private static final List<String> EXCLUDE_URLS = Arrays.asList(
            "/user/login",
            "/user/register"
    );

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
        // 从上下文获取UserDetailsService（或通过构造器注入，更推荐）
        this.userDetailsService = SpringContextHolder.getBean(UserDetailsService.class);
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // 步骤1：判断是否是放行接口，如果是直接放行
        String requestURI = request.getRequestURI();
        String method = request.getMethod();
        // 仅放行POST方法的登录/注册接口（和Security配置保持一致）
        if (HttpMethod.POST.name().equals(method) && EXCLUDE_URLS.contains(requestURI)) {
            filterChain.doFilter(request, response);
            return; // 跳过后续token校验逻辑
        }
        //步骤2：非放行接口，执行token校验逻辑（原有逻辑保留） 从请求头中获取token
        String jwtToken = getTokenFromRequest(request);

        if (StringUtils.isBlank(jwtToken)) {
            // 无token，交给异常处理器返回"未携带token"
            filterChain.doFilter(request, response);
            return; //结束方法
        }

        try {
            //token可用
            String userName = getSubject(jwtToken);
            if (StringUtils.isNotBlank(userName) && SecurityContextHolder.getContext().getAuthentication() == null) {
                // 加载用户信息
                UserDetails userDetails = userDetailsService.loadUserByUsername(userName);
                // 验证token有效性（根据你的JWT工具类调整）
                boolean isValid = JwtGeneratorUtil.verifyAccessToken(jwtToken, TokenConstant.SIGN_KEY);
                if (isValid) {
                    // 设置认证信息到上下文
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }
        } catch (Exception e) {
            log.error("Token校验失败：{}", e.getMessage());
        }
        // 继续执行过滤器链
        filterChain.doFilter(request, response);
    }


    /**
     * 从请求头中获取token（如Bearer token格式）
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (com.mate.cloud.core.util.StringUtils.isNotBlank(authHeader) && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7); // 去掉Bearer前缀
        }
        return null;
    }

}