package com.projectManagement.taskflow.controller;

import com.projectManagement.taskflow.entity.ProfileEntity;
import com.projectManagement.taskflow.repository.ProfileRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/profile")
@RestController
public class ProfileController {

    @Autowired
    private ProfileRepo profileRepo;
//    http://localhost:8080/api/profile/5
    @GetMapping("/{userId}")
    private ProfileEntity getProfile(@PathVariable Long userId){
        return profileRepo.findByUserId(userId).orElseThrow(()->new RuntimeException("Profile not found"));
    }

    @PutMapping
    private ProfileEntity addProfile(@RequestBody ProfileEntity profile){
        return profileRepo.save(profile);
    }
}
