package com.projectManagement.taskflow.service;

import com.projectManagement.taskflow.entity.ProfileEntity;
import com.projectManagement.taskflow.repository.ProfileRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProfileService {

    @Autowired
    private ProfileRepo profileRepo;

    public ProfileEntity getProfileByUserId(Long userId){
        return profileRepo.findByUserId(userId).orElseThrow(()-> new RuntimeException("Profile not found for the User Id"));
    }

}
