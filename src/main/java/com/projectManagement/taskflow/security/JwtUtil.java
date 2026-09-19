package com.projectManagement.taskflow.security;

import com.projectManagement.taskflow.enums.RoleEnum;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    public static final String CLAIM_ROLE = "role";
    public static final String CLAIM_TENANT = "tenantId";
    public static final String CLAIM_TYPE = "type";
    public static final String TYPE_ACCESS = "access";
    public static final String TYPE_REFRESH = "refresh";

    /** HS256 requires a key of at least 256 bits. */
    private static final int MIN_SECRET_BYTES = 32;

    private final SecretKey signKey;
    private final long accessTokenValidityMs;
    private final long refreshTokenValidityMs;

    public JwtUtil(@Value("${app.jwt.secret}") String secret,
                   @Value("${app.jwt.access-token-validity-seconds:3600}") long accessTokenValiditySeconds,
                   @Value("${app.jwt.refresh-token-validity-seconds:604800}") long refreshTokenValiditySeconds) {
        if (secret == null || secret.isBlank() || secret.getBytes(StandardCharsets.UTF_8).length < MIN_SECRET_BYTES) {
            throw new IllegalStateException(
                    "app.jwt.secret must be set to a value of at least " + MIN_SECRET_BYTES +
                    " bytes. Provide it through the JWT_SECRET environment variable.");
        }
        this.signKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenValidityMs = accessTokenValiditySeconds * 1000;
        this.refreshTokenValidityMs = refreshTokenValiditySeconds * 1000;
    }

    public Map<String, String> generateTokens(String username, RoleEnum role) {
        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", generateAccessToken(username, role));
        tokens.put("refreshToken", generateRefreshToken(username));
        return tokens;
    }

    public String generateAccessToken(String username, RoleEnum role) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .setSubject(username)
                .claim(CLAIM_TYPE, TYPE_ACCESS)
                .claim(CLAIM_ROLE, role)
                .claim(CLAIM_TENANT, tenantOf(username))
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + accessTokenValidityMs))
                .signWith(signKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(String username) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .setSubject(username)
                .claim(CLAIM_TYPE, TYPE_REFRESH)
                .claim(CLAIM_TENANT, tenantOf(username))
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + refreshTokenValidityMs))
                .signWith(signKey, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Parses and verifies the token signature.
     *
     * @throws io.jsonwebtoken.ExpiredJwtException if the token is expired
     * @throws io.jsonwebtoken.JwtException        if the token is malformed, has a bad signature, etc.
     */
    public Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean isAccessToken(Claims claims) {
        // Tokens issued before the type claim existed are treated as access tokens.
        String type = claims.get(CLAIM_TYPE, String.class);
        return type == null || TYPE_ACCESS.equals(type);
    }

    public boolean isRefreshToken(Claims claims) {
        return TYPE_REFRESH.equals(claims.get(CLAIM_TYPE, String.class));
    }

    private static String tenantOf(String username) {
        int slash = username.indexOf('/');
        return slash > 0 ? username.substring(0, slash) : null;
    }
}
