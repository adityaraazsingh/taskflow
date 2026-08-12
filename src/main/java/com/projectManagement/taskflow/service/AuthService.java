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

    @Autowired
    private TenantIdentifierResolver tenantIdentifierResolver;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public UserResponseDto register(TenantUserRequestDto dto){
        System.out.println(this.getClass());
        System.out.println(
                "TX ACTIVE: " +
                        TransactionSynchronizationManager.isActualTransactionActive()
        );
        try {
            TenantContext.setTenant(dto.getTenantName());
            System.out.println("REGISTER TENANT: " + TenantContext.getTenant());
            System.out.println(
                    "REGISTER: " + TenantContext.getTenant()
                            + " THREAD: " + Thread.currentThread().getName()
            );
            UserRequestDTO userDto = new UserRequestDTO();
            userDto.setEmail(dto.getEmail());
            userDto.setRole(dto.getRole());
            userDto.setUsername(dto.getUsername());

            String passwordHash = passwordEncoder.encode(dto.getPassword());
            System.out.println("BEFORE SAVE TENANT: " + TenantContext.getTenant());

            UserEntity user = userMapper.toEntity(userDto, passwordHash);
            System.out.println("AFTER SAVE TENANT: " + TenantContext.getTenant());

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

//{
//        "accessToken": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJURU5BTlQyMC9hZGkiLCJyb2xlIjoiQURNSU4iLCJ0ZW5hbnRJZCI6IlRFTkFOVDIwIiwiaWF0IjoxNzg2NDE0NjIwLCJleHAiOjE3ODY0MTgyMjB9.lwWdspSGV67gFeY93Z2PBtmxpbQCyGEOJ1o30iNBlIE",
//        "refreshToken": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJURU5BTlQyMC9hZGkiLCJpYXQiOjE3ODY0MTQ2MjAsImV4cCI6MTc4NzAxOTQyMH0.moCDblmQdzYyxip8Z382nWvRQACC3CjwJYJcHYQgh9g",
//        "role": "ADMIN",
//        "username": "adi"
//        }

//{
//        "accessToken": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJURU5BTlQyMS9hZGkiLCJyb2xlIjoiQURNSU4iLCJ0ZW5hbnRJZCI6IlRFTkFOVDIxIiwiaWF0IjoxNzg2NDE1MDc1LCJleHAiOjE3ODY0MTg2NzV9.lb-i6SsMCyx4ucOL3oqEMFKHTEx04gbe7AArxhzpFic",
//        "refreshToken": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJURU5BTlQyMS9hZGkiLCJpYXQiOjE3ODY0MTUwNzUsImV4cCI6MTc4NzAxOTg3NX0.iEx5GHmuwdJeaUeKETsWbuGm0jtyYxVNWbVs-ejMgBM",
//        "role": "ADMIN",
//        "username": "adi"
//        }