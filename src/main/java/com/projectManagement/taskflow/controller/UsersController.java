package com.projectManagement.taskflow.controller;

import com.projectManagement.taskflow.dto.PageResponseDto;
import com.projectManagement.taskflow.dto.TenantUserRequestDto;
import com.projectManagement.taskflow.dto.UserResponseDto;
import com.projectManagement.taskflow.service.AuthService;
import com.projectManagement.taskflow.service.TenantService;
import com.projectManagement.taskflow.service.UserService;
import com.projectManagement.taskflow.tenant.TenantContext;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/users")
@RestController
public class UsersController {

    private final UserService userService;
    private final AuthService authService;
    private final TenantService tenantService;

    public UsersController(UserService userService, AuthService authService, TenantService tenantService) {
        this.userService = userService;
        this.authService = authService;
        this.tenantService = tenantService;
    }

    @GetMapping
    private List<UserResponseDto> alLusers(){
        return userService.findAllUsers();
    }

    @GetMapping("/{userId}")
    public UserResponseDto getUserByUserId(@PathVariable Long userId){
        return this.userService.findById(userId);
    }

    @GetMapping("/me/{username}")
    public ResponseEntity<UserResponseDto> getUserDetails(@PathVariable String username){
        String fullUsername = TenantContext.getTenant()+"/"+username;
        return ResponseEntity.ok(userService.findByUsername(fullUsername));
    }

    @PostMapping("/signup")
    public ResponseEntity<UserResponseDto> registerUser(@Valid @RequestBody TenantUserRequestDto dto){
        tenantService.createTenant(dto.getTenantName());
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(dto));
    }

    @GetMapping("/all")
    public PageResponseDto<UserResponseDto> getAllUsers(@RequestParam(defaultValue = "0") int page,
                                                        @RequestParam(defaultValue = "10") int size){
        Pageable pageable = PageRequest.of(page, size);
        return userService.listUsers(pageable);
    }
}
