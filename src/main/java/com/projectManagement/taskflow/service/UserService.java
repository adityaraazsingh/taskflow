package com.projectManagement.taskflow.service;

import com.projectManagement.taskflow.entity.UserEntity;
import com.projectManagement.taskflow.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepo userRepo;

    public UserEntity findById(Long id){
        return userRepo.findById(id).get();
    }

    public UserEntity findByUsername(String username){
        return userRepo.findByUsername(username).
                orElseThrow(()-> new RuntimeException("User Not Found"));
    }

    public UserEntity updateProfile(Long id,UserEntity updatedProfile){
        return userRepo.save(updatedProfile);
    }

    public Boolean changePassword(Long id , String password){
//     TODO : Add password Encoder here
        String encodedPassword = password;

        String user = String.valueOf(userRepo.findById(id));
        if(user!=null) return true;
        else return false;

    }

//    TODO: BluePrint.md -> listUsers(Pageable) — admin only
    public List<UserEntity> listUsers(){
        return userRepo.findAll();
    }

}
