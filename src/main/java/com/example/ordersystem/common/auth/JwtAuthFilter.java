package com.example.ordersystem.common.auth;

import jakarta.servlet.*;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAuthFilter extends GenericFilter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
//        token검증

//        token분해 후 Authentication객체 생성

//        다시 filterchain으로 되돌아가는 로직
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
