package com.projectManagement.taskflow.service;

import com.projectManagement.taskflow.dto.LoginCredentials;
import com.projectManagement.taskflow.enums.RoleEnum;
import com.projectManagement.taskflow.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;


    public void register(){

    }

    public String login( LoginCredentials loginCredentials){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginCredentials.getUsername(),
                        loginCredentials.getPassword()
                )
        );
        if(authentication.isAuthenticated()){
            RoleEnum role = userService.findByUsername(loginCredentials.getUsername()).getRole();
            return jwtUtil.generateToken(
                    loginCredentials.getUsername(),role);
        }else{
            throw new RuntimeException("Invalid Credentials");
        }
    }

    public void getCurrentUser(){

    }
}
