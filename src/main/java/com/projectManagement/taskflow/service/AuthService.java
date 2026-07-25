package com.projectManagement.taskflow.service;

import com.projectManagement.taskflow.dto.AuthResponse;
import com.projectManagement.taskflow.dto.LoginCredentials;
import com.projectManagement.taskflow.dto.UserRequestDTO;
import com.projectManagement.taskflow.dto.UserResponseDto;
import com.projectManagement.taskflow.entity.UserEntity;
import com.projectManagement.taskflow.enums.RoleEnum;
import com.projectManagement.taskflow.exception.InvalidCredentialsException;
import com.projectManagement.taskflow.exception.UserNotFoundException;
import com.projectManagement.taskflow.mapper.UserMapper;
import com.projectManagement.taskflow.repository.UserRepo;
import com.projectManagement.taskflow.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserRepo userRepo;


    public UserResponseDto register(UserRequestDTO dto){
        String password = dto.getPassword();
        String passwordHash = passwordEncoder.encode(password);
        UserEntity user = userMapper.toEntity(dto, passwordHash);
        UserResponseDto userDto = userMapper.toDto(userRepo.save(user));
        return userDto;
    }

    public AuthResponse login(LoginCredentials loginCredentials){
        AuthResponse authResponse = new AuthResponse();
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginCredentials.getUsername(),
                        loginCredentials.getPassword()
                )
        );
        if(authentication.isAuthenticated()){
            String username = loginCredentials.getUsername();
            RoleEnum role = userRepo.findByUsername(username)
                    .orElseThrow(()->new UserNotFoundException("User not found"))
                    .getRole();
            authResponse.setToken(jwtUtil.generateToken(
                    loginCredentials.getUsername(),role));
            authResponse.setRole(role);
            authResponse.setUsername(username);
            return authResponse;
        }else{
            throw new InvalidCredentialsException("Invalid Credentials");
        }
    }

    public UserEntity getCurrentUser(){
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;
        if(principal instanceof UserDetails){
            username = ((UserDetails) principal).getUsername();
        }else{
            username = principal.toString();
        }

        return userRepo.findByUsername(username).orElseThrow(()->new UserNotFoundException("User not found"));
    }

    //Todo: Write a IsExpired Method to check weather stored JWT in local storage has expired or not
}
