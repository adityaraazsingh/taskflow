package com.projectManagement.taskflow.controller;

import com.projectManagement.taskflow.entity.UserEntity;
import com.projectManagement.taskflow.repository.UserRepo;
import com.projectManagement.taskflow.service.UserService;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/users")
@RestController
public class UsersController {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private UserService userService;

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
    private UserEntity registerUser(@RequestBody UserEntity user){
        return userRepo.save(user);
    }
}
