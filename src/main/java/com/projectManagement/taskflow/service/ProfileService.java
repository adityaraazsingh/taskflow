package com.projectManagement.taskflow.service;

import com.projectManagement.taskflow.entity.ProfileEntity;
import com.projectManagement.taskflow.repository.ProfileRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional
@Service
public class ProfileService {

    @Autowired
    private ProfileRepo profileRepo;

    @Transactional(readOnly = true)
    public ProfileEntity getProfileByUserId(Long userId){
        return profileRepo.findByUserId(userId).orElseThrow(()-> new RuntimeException("Profile not found for the User Id"));
    }

    public ProfileEntity saveProfile(ProfileEntity profile){
        Optional<ProfileEntity> userProfileOpt = profileRepo.findByUserId(profile.getUserId());
        if (userProfileOpt.isPresent()) {
            ProfileEntity userProfile = userProfileOpt.get();
            userProfile.setBio(profile.getBio());
            userProfile.setFirstName(profile.getFirstName());
            userProfile.setLastName(profile.getLastName());
            return profileRepo.save(userProfile);
        } else {
            return profileRepo.save(profile);
        }
    }

}
