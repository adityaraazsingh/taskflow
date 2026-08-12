package com.projectManagement.taskflow.tenant;

import com.projectManagement.taskflow.security.JwtUtil;
import io.jsonwebtoken.io.IOException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class TenantFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        try {
            String authHeader = request.getHeader("Authorization");

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);

                // extract tenantId from JWT
                String tenantId = jwtUtil.extractClaim(token, claims ->
                        claims.get("tenantId", String.class)
                );

                if (tenantId != null) {
                    TenantContext.setTenant(tenantId);
                    System.out.println("TENANT SET: " + tenantId);
                }
            }

            filterChain.doFilter(request, response);

        } catch (java.io.IOException e) {
            throw new RuntimeException(e);
        } finally {
            // 🔥 VERY IMPORTANT: avoid tenant leakage between requests
            TenantContext.clear();
        }
    }
}
