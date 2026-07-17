package com.projectManagement.taskflow.controller;

import com.projectManagement.taskflow.dto.UserRequestDTO;
import com.projectManagement.taskflow.entity.UserEntity;
import com.projectManagement.taskflow.mapper.UserMapper;
import com.projectManagement.taskflow.repository.UserRepo;
import com.projectManagement.taskflow.service.AuthService;
import com.projectManagement.taskflow.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/users")
@RestController
public class UsersController {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AuthService authService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @GetMapping
    private List<UserEntity> alLusers(){
        return userRepo.findAll();
    }

//    http://localhost:8080/api/users/me/User
    @GetMapping("/me/{username}")
    private ResponseEntity<UserEntity> getUserDetails(@PathVariable String username){
        return ResponseEntity.ok(userService.findByUsername(username));
    }

    @PostMapping("/signup")
    private ResponseEntity<UserEntity> registerUser(@RequestBody UserRequestDTO dto){
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(dto));
    }

    @GetMapping("/all")
    private Page<UserEntity> getAllUsers(@RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "10") int size){
        Pageable pageable = PageRequest.of(page, size);
        return userService.listUsers(pageable);

    }
}
