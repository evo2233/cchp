package org.example.demo.authentication;

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
            "/admin/login"
    );

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws ServletException, IOException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String requestURI = request.getRequestURI();

        for (String path : whitelist) {
            if (requestURI.contains(path)) {
                log.info("Allow request for: {}", path);
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
        filterChain.doFilter(request, response); // allow access
    }
}
