package com.projectManagement.taskflow.service;

import com.projectManagement.taskflow.dto.*;
import com.projectManagement.taskflow.entity.UserEntity;
import com.projectManagement.taskflow.enums.RoleEnum;
import com.projectManagement.taskflow.exception.InvalidCredentialsException;
import com.projectManagement.taskflow.exception.UserNotFoundException;
import com.projectManagement.taskflow.mapper.UserMapper;
import com.projectManagement.taskflow.repository.UserRepo;
import com.projectManagement.taskflow.security.JwtUtil;
import com.projectManagement.taskflow.tenant.TenantContext;
import com.projectManagement.taskflow.tenant.TenantIdentifierResolver;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.springframework.aop.framework.AopContext;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.Map;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserRepo userRepo;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public UserResponseDto register(TenantUserRequestDto dto){
        try {
            TenantContext.setTenant(dto.getTenantName());
            UserRequestDTO userDto = new UserRequestDTO();
            userDto.setEmail(dto.getEmail());
            userDto.setRole(dto.getRole());
            userDto.setUsername(dto.getUsername());

            String passwordHash = passwordEncoder.encode(dto.getPassword());
            UserEntity user = userMapper.toEntity(userDto, passwordHash);
            UserEntity saved = userRepo.save(user);
            userRepo.flush();   // 🔥 FORCE DB interaction

            return userMapper.toDto(saved);

        } finally {
            TenantContext.clear();
        }
    }

    public AuthResponse login(LoginCredentials loginCredentials){
        AuthResponse authResponse = new AuthResponse();
        try{
            String fullUsername = loginCredentials.getUsername();
            if (fullUsername == null || fullUsername.indexOf('/') <= 0 || fullUsername.endsWith("/")) {
                throw new InvalidCredentialsException("Username must be in the form <tenant>/<username>");
            }
            String tenantId = fullUsername.substring(0, fullUsername.indexOf('/'));
            String username = fullUsername.substring(fullUsername.indexOf('/') + 1);
            TenantContext.setTenant(tenantId);
            Authentication authentication;
            try {
                authentication = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                loginCredentials.getUsername(),
                                loginCredentials.getPassword()
                        )
                );
            } catch (AuthenticationException e) {
                // Wrong password, unknown user or missing tenant schema all look the same to the client.
                throw new InvalidCredentialsException("Invalid credentials");
            }
            if (authentication.isAuthenticated()) {
                RoleEnum role = userRepo.findByUsername(loginCredentials.getUsername())
                        .orElseThrow(() -> new UserNotFoundException("User not found"))
                        .getRole();
                Map<String, String> tokens = jwtUtil.generateTokens(loginCredentials.getUsername(), role);
                authResponse.setAccessToken(tokens.get("accessToken"));
                authResponse.setRefreshToken(tokens.get("refreshToken"));
                authResponse.setRole(role);
                authResponse.setUsername(username);
                return authResponse;
            } else {
                throw new InvalidCredentialsException("Invalid Credentials");
            }
        }finally {
            TenantContext.clear();
        }
    }

    /**
     * Exchanges a valid refresh token for a new token pair. The user's role is read from the
     * database so a refresh never silently changes (or downgrades) their permissions.
     */
    public AuthResponse refresh(String refreshToken){
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new InvalidCredentialsException("Refresh token is required");
        }
        try {
            Claims claims = jwtUtil.parseClaims(refreshToken);
            if (!jwtUtil.isRefreshToken(claims)) {
                throw new InvalidCredentialsException("Invalid refresh token");
            }
            String fullUsername = claims.getSubject();
            if (fullUsername == null || fullUsername.indexOf('/') <= 0) {
                throw new InvalidCredentialsException("Invalid refresh token");
            }
            TenantContext.setTenant(fullUsername.substring(0, fullUsername.indexOf('/')));

            UserEntity user = userRepo.findByUsername(fullUsername)
                    .orElseThrow(() -> new InvalidCredentialsException("Account no longer exists, please sign in again"));

            Map<String, String> tokens = jwtUtil.generateTokens(fullUsername, user.getRole());
            AuthResponse authResponse = new AuthResponse();
            authResponse.setAccessToken(tokens.get("accessToken"));
            authResponse.setRefreshToken(tokens.get("refreshToken"));
            authResponse.setRole(user.getRole());
            authResponse.setUsername(fullUsername.substring(fullUsername.indexOf('/') + 1));
            return authResponse;
        } catch (JwtException | IllegalArgumentException e) {
            throw new InvalidCredentialsException("Invalid or expired refresh token");
        } finally {
            TenantContext.clear();
        }
    }

    @Transactional
    public UserEntity getCurrentUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new InvalidCredentialsException("Not authenticated");
        }
        Object principal = authentication.getPrincipal();
        String username;
        if(principal instanceof UserDetails){
            username = ((UserDetails) principal).getUsername();
        }else{
            username = principal.toString();
        }

        return userRepo.findByUsername(username).orElseThrow(()->new UserNotFoundException("User not found"));
    }

    //Todo: Write a IsExpired Method to check weather stored JWT in local storage has expired or not
}