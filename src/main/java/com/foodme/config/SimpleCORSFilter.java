package com.foodme.config;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * This filter enables cross-domain requests by sending corresponding headers
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SimpleCORSFilter extends OncePerRequestFilter {

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        response.setHeader("Access-Control-Allow-Origin", "*"); //req.getHeader("Origin"));
        response.setHeader("Access-Control-Allow-Methods", "PUT, POST, GET, OPTIONS, DELETE, PATCH");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers",
                "accept, authorization, content-type, x_csrf_token, Cache-Control, X-Requested-With, Content-Length");
        response.setHeader("Access-Control-Allow-Credentials", "true");

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            chain.doFilter(request, response);
        }
    }
}
