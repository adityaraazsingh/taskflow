package com.projectManagement.taskflow.controller;

import com.projectManagement.taskflow.dto.LoginCredentials;
import com.projectManagement.taskflow.dto.UserRequestDTO;
import com.projectManagement.taskflow.entity.UserEntity;
import com.projectManagement.taskflow.mapper.UserMapper;
import com.projectManagement.taskflow.repository.UserRepo;
import com.projectManagement.taskflow.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
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
    private BCryptPasswordEncoder passwordEncoder;

    @GetMapping
    private List<UserEntity> alLusers(){
        return userRepo.findAll();
    }

//    http://localhost:8080/api/users/me/User
    @GetMapping("/me/{username}")
    private UserEntity getUserDetails(@PathVariable String username){
        return userService.findByUsername(username);
    }

    @PostMapping("/me")
    private UserEntity registerUser(@RequestBody UserRequestDTO dto){
        String password = dto.getPassword();
        String passwordHash = passwordEncoder.encode(password);
        UserEntity user = userMapper.toEntity(dto, passwordHash);
        return userRepo.save(user);
    }
}
