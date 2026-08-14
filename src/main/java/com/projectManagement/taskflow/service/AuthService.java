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
import org.springframework.aop.framework.AopContext;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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
            String tenantId = loginCredentials.getUsername().split("/")[0];
            String username = loginCredentials.getUsername().split("/")[1];
            if (tenantId != null) {
                TenantContext.setTenant(tenantId);
            }
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginCredentials.getUsername(),
                            loginCredentials.getPassword()
                    )
            );
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

    @Transactional
    public UserEntity getCurrentUser(){
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
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