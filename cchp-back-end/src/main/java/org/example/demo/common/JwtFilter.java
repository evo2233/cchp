package org.example.demo.common;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

// 过滤器统一拦截请求
@Slf4j
@WebFilter(urlPatterns = "/*")
public class JwtFilter implements Filter {

    private final List<String> whitelist = Arrays.asList(
            "/patient/login",
            "/patient/register",
            "/admin",
            "/doctor/login"
    );

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws ServletException, IOException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        // 设置跨域响应头（即使失败也要设置）
        response.setHeader("Access-Control-Allow-Origin", "http://113.44.76.249");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type");
        response.setHeader("Access-Control-Expose-Headers", "Authorization");
        response.setHeader("Access-Control-Allow-Credentials", "true");

        // 放行OPTIONS请求（预检请求）
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        String requestURI = request.getRequestURI();

        for (String path : whitelist) {
            if (requestURI.startsWith(path)) {
                log.info("Allow request for: {}", requestURI);
                filterChain.doFilter(request, response);
                return;
            }
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.info("Authorization header not found");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        authHeader = authHeader.replace("Bearer ", "");
        try {
            JwtUtils.parseToken(authHeader);
        } catch (Exception e) {
            log.info("Invalid JWT token");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        filterChain.doFilter(request, response);
    }

}
