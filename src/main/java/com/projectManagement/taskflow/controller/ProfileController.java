package com.projectManagement.taskflow.controller;

import com.projectManagement.taskflow.entity.ProfileEntity;
import com.projectManagement.taskflow.service.ProfileService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/profile")
@RestController
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }


    @GetMapping("/{userId}")
    public ProfileEntity getProfile(@PathVariable Long userId){
        return profileService.getProfileByUserId(userId);
    }

    @PutMapping
    public ProfileEntity addProfile(@Valid @RequestBody ProfileEntity profile){
        return profileService.saveProfile(profile);
    }
}
