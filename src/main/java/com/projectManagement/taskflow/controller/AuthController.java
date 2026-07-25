package com.projectManagement.taskflow.controller;

import com.projectManagement.taskflow.dto.AuthResponse;
import com.projectManagement.taskflow.dto.ChangePasswordRequestDto;
import com.projectManagement.taskflow.dto.LoginCredentials;
import com.projectManagement.taskflow.entity.UserEntity;
import com.projectManagement.taskflow.enums.RoleEnum;
import com.projectManagement.taskflow.security.JwtUtil;
import com.projectManagement.taskflow.service.AuthService;
import com.projectManagement.taskflow.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginCredentials loginCredentials){
       return ResponseEntity.ok(authService.login(loginCredentials));
    }

    @GetMapping("/me")
    public ResponseEntity<UserEntity> getCurrentUser(){
        return ResponseEntity.ok(authService.getCurrentUser());
    }

    @PostMapping("/password")
    public ResponseEntity<Boolean> changePassword(@RequestBody ChangePasswordRequestDto changePasswordRequest){
        return ResponseEntity.ok(userService.changePassword(changePasswordRequest));
    }
}
