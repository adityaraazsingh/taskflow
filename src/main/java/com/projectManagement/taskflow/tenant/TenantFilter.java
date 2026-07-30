package com.projectManagement.taskflow.tenant;

import com.projectManagement.taskflow.security.JwtUtil;
import com.projectManagement.taskflow.tenant.TenantContext;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class TenantFilter extends OncePerRequestFilter {

    // reuse whatever service you already use to parse the JWT
    private final JwtUtil jwtService;

    public TenantFilter(JwtUtil jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = extractToken(request);
            if (token != null) {
                Claims claims = jwtService.parseClaims(token);
                String tenantId = claims.get("tenantId", String.class);
                if (tenantId != null) {
                    TenantContext.setTenant(tenantId);
                }
            }
            filterChain.doFilter(request, response);
        } finally {
            TenantContext.clear(); // critical - avoid leaking tenant across threads in the pool
        }
    }

    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        return (header != null && header.startsWith("Bearer ")) ? header.substring(7) : null;
    }
}