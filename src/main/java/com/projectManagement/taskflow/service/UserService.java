package com.projectManagement.taskflow.service;

import com.projectManagement.taskflow.dto.ChangePasswordRequestDto;
import com.projectManagement.taskflow.dto.PageResponseDto;
import com.projectManagement.taskflow.dto.UserRequestDTO;
import com.projectManagement.taskflow.dto.UserResponseDto;
import com.projectManagement.taskflow.entity.UserEntity;
import com.projectManagement.taskflow.enums.RoleEnum;
import com.projectManagement.taskflow.exception.AccessDeniedException;
import com.projectManagement.taskflow.exception.UserNotFoundException;
import com.projectManagement.taskflow.mapper.PageMapper;
import com.projectManagement.taskflow.mapper.UserMapper;
import com.projectManagement.taskflow.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {

    private final UserRepo userRepo;
    private final AuthService authService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final PageMapper pageMapper;

    public UserService(UserRepo userRepo, AuthService authService, BCryptPasswordEncoder passwordEncoder, UserMapper userMapper, PageMapper pageMapper) {
        this.userRepo = userRepo;
        this.authService = authService;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
        this.pageMapper = pageMapper;
    }

    @Transactional(readOnly = true)
    public UserResponseDto findById(Long id){
        return userMapper.toDto(userRepo.findById(id)
                .orElseThrow(()-> new UserNotFoundException("User not found")));
    }

    public List<UserResponseDto> findAllUsers(){
        return userRepo.findAll().stream().map(userMapper::toDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UserResponseDto findByUsername(String username){
        UserEntity entity = userRepo.findByUsername(username).
                orElseThrow(()-> new UserNotFoundException("User Not Found"));
        return userMapper.toDto(entity);
    }

    public UserResponseDto updateProfile(Long id, UserRequestDTO updatedProfile){
        UserEntity entity = authService.getCurrentUser();
        entity.setUsername(updatedProfile.getUsername());
        entity.setRole(updatedProfile.getRole());
        entity.setEmail(updatedProfile.getEmail());
        entity = userRepo.save(entity);
        return userMapper.toDto(entity);
    }

    public Boolean changePassword(ChangePasswordRequestDto dto){
        UserEntity user = authService.getCurrentUser();
        System.out.println(user.getPasswordHash());
        System.out.println(passwordEncoder.encode(dto.getCurrentPassword()));
        String encodedPassword = passwordEncoder.encode(dto.getNewPassword());
        user.setPasswordHash(encodedPassword);
        userRepo.save(user);
        return true;
    }

    @Transactional(readOnly = true)
    public PageResponseDto<UserResponseDto> listUsers(Pageable pageable){
        UserEntity requester = authService.getCurrentUser();
        if(requester.getRole()== RoleEnum.ADMIN){
            Page<UserEntity> users = userRepo.findAll(pageable);
            return pageMapper.toDto(users.map(userMapper::toDto));
        }else{
            throw new AccessDeniedException("User Not Allowed to do this Action");
        }
    }

}
