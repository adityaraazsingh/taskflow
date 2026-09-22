package com.projectManagement.taskflow.security;

import com.projectManagement.taskflow.tenant.TenantContext;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtFilter.class);

    /** Request attribute holding a human readable reason why the bearer token was rejected. */
    public static final String AUTH_ERROR_ATTRIBUTE = JwtFilter.class.getName() + ".AUTH_ERROR";

    private static final String BEARER_PREFIX = "Bearer ";

    /** Endpoints that must never be influenced by a (possibly stale) bearer token sent by the client. */
    private static final Set<String> PUBLIC_PATHS = Set.of(
            "/api/auth/login",
            "/api/auth/refresh",
            "/api/users/signup"
    );

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    public JwtFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        if (HttpMethod.OPTIONS.matches(request.getMethod())) {
            return true;
        }
        String path = request.getRequestURI();
        if (request.getContextPath() != null && !request.getContextPath().isEmpty()) {
            path = path.substring(request.getContextPath().length());
        }
        return PUBLIC_PATHS.contains(path) || path.startsWith("/h2-console");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(BEARER_PREFIX.length()).trim();
        try {
            authenticate(token, request);
            filterChain.doFilter(request, response);
        } finally {
            TenantContext.clear();
        }
    }

    /**
     * Populates the SecurityContext when the token is valid. On any failure the request simply
     * continues unauthenticated; Spring Security's entry point then answers with 401 for protected
     * resources. Nothing is thrown out of the filter, so no failure is ever masked as a 403 by the
     * secured /error dispatch.
     */
    private void authenticate(String token, HttpServletRequest request) {
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            return;
        }
        try {
            Claims claims = jwtUtil.parseClaims(token);
            if (!jwtUtil.isAccessToken(claims)) {
                reject(request, "Refresh tokens cannot be used to access the API");
                return;
            }

            String username = claims.getSubject();
            if (username == null || username.isBlank()) {
                reject(request, "Token has no subject");
                return;
            }

            String tenantId = claims.get(JwtUtil.CLAIM_TENANT, String.class);
            if (tenantId == null && username.contains("/")) {
                tenantId = username.substring(0, username.indexOf('/'));
            }
            if (tenantId != null) {
                TenantContext.setTenant(tenantId);
                MDC.put("tenantId", tenantId);
            }

            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            List<GrantedAuthority> authorities = new ArrayList<>();
            String role = claims.get(JwtUtil.CLAIM_ROLE, String.class);
            if (role != null) {
                authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
            }

            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);
        } catch (ExpiredJwtException ex) {
            reject(request, "Access token expired");
        } catch (JwtException | IllegalArgumentException ex) {
            // Malformed token, bad signature (e.g. secret rotated), unsupported algorithm, ...
            reject(request, "Invalid access token");
        } catch (RuntimeException ex) {
            // User no longer exists, tenant schema missing (e.g. database was reset), DB unavailable, ...
            log.warn("Could not authenticate bearer token for {} {}: {}",
                    request.getMethod(), request.getRequestURI(), ex.toString());
            TenantContext.clear();
            reject(request, "Account could not be verified, please sign in again");
        }
    }

    private void reject(HttpServletRequest request, String reason) {
        log.debug("Rejected bearer token on {} {}: {}", request.getMethod(), request.getRequestURI(), reason);
        request.setAttribute(AUTH_ERROR_ATTRIBUTE, reason);
    }
}
