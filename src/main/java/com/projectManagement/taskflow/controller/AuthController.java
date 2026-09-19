package com.projectManagement.taskflow.controller;

import com.projectManagement.taskflow.dto.AuthResponse;
import com.projectManagement.taskflow.dto.ChangePasswordRequestDto;
import com.projectManagement.taskflow.dto.LoginCredentials;
import com.projectManagement.taskflow.entity.UserEntity;
import com.projectManagement.taskflow.service.AuthService;
import com.projectManagement.taskflow.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    public AuthController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginCredentials loginCredentials){
       return ResponseEntity.ok(authService.login(loginCredentials));
    }

    @GetMapping("/me")
    public ResponseEntity<UserEntity> getCurrentUser(){
        return ResponseEntity.ok(authService.getCurrentUser());
    }

    @PostMapping("/password")
    public ResponseEntity<Boolean> changePassword(@Valid @RequestBody ChangePasswordRequestDto changePasswordRequest){
        return ResponseEntity.ok(userService.changePassword(changePasswordRequest));
    }

    /** Returns a fresh access + refresh token pair; 401 (via InvalidCredentialsException) when the refresh token is unusable. */
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody Map<String, String> request) {
        return ResponseEntity.ok(authService.refresh(request.get("refreshToken")));
    }

}
