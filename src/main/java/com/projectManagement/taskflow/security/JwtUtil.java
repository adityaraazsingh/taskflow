package com.projectManagement.taskflow.security;

import com.projectManagement.taskflow.enums.RoleEnum;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    private final String SECRET="IAMTHEBESTSTRONESTMANPRESENTONETHEEARTHIWILLCHANGEEVERYTHING";

    private Key getSignKey(){
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    public Map<String, String> generateTokens(String username, RoleEnum role) {
        String accessToken = Jwts.builder()
                .setSubject(username)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 )) // 15 min
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
        String refreshToken = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7)) // 7 days
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();

        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", refreshToken);
        return tokens;
    }


    public String extractUsername(String token){
        try{
            return Jwts.parserBuilder()
                    .setSigningKey(getSignKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        }catch(ExpiredJwtException ex){
            return null;
        }
    }

    public String getRole(String token){
        try{
            String role = Jwts.parserBuilder()
                    .setSigningKey(getSignKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .get("role",String.class);
            System.out.println("Role of the user "+ role);
            return role;
        }catch(ExpiredJwtException ex){
            return null;
        }
    }

    public boolean validateToken(String token, String username){
        return username.equals(extractUsername(token)) && !isTokenExpired(token);
    }


    public boolean isTokenExpired(String token) {
        try {
            Date expiration = Jwts.parserBuilder()
                    .setSigningKey(getSignKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getExpiration();
            return expiration.before(new Date());
        } catch (ExpiredJwtException e) {
            return true; // token is expired
        }
    }

}
