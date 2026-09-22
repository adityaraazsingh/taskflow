package com.projectManagement.taskflow.exception;

// src/main/java/com/projectManagement/taskflow/filter/CorrelationFilter.java
import com.projectManagement.taskflow.tenant.TenantContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorrelationFilter extends OncePerRequestFilter {
    public static final String HEADER_REQUEST_ID = "X-Request-Id";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {
        String traceId = request.getHeader(HEADER_REQUEST_ID);
        if (traceId == null || traceId.isBlank()) {
            traceId = UUID.randomUUID().toString();
        }

        String uri = request.getRequestURI();

        MDC.put("traceId", traceId); MDC.put("instance", uri);

        response.setHeader(HEADER_REQUEST_ID, traceId);

        try {
            chain.doFilter(request, response);
        } finally {
            MDC.remove("traceId"); MDC.remove("instance");
        }
    }
}