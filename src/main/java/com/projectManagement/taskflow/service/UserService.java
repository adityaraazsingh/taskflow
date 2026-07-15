package com.projectManagement.taskflow.service;

import com.projectManagement.taskflow.entity.UserEntity;
import com.projectManagement.taskflow.enums.RoleEnum;
import com.projectManagement.taskflow.exception.AccessDeniedException;
import com.projectManagement.taskflow.exception.UserNotFoundException;
import com.projectManagement.taskflow.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private AuthService authService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public UserEntity findById(Long id){
        return userRepo.findById(id).get();
    }

    public UserEntity findByUsername(String username){
        return userRepo.findByUsername(username).
                orElseThrow(()-> new UserNotFoundException("User Not Found"));
    }

    public UserEntity updateProfile(Long id,UserEntity updatedProfile){
        return userRepo.save(updatedProfile);
    }

    public Boolean changePassword(Long id , String password){
        String encodedPassword = passwordEncoder.encode(password);
        UserEntity user = userRepo.findById(id).orElseThrow(()->new UserNotFoundException("User Not Found"));
        user.setPasswordHash(encodedPassword);
        userRepo.save(user);
        return user != null;

    }

    public Page<UserEntity> listUsers(Pageable pageable){
        UserEntity requester = authService.getCurrentUser();
        if(requester.getRole()== RoleEnum.ADMIN){
            return userRepo.findAll(pageable);
        }else{
            throw new AccessDeniedException("User Not Allowed to do this Action");
        }
    }

}
