package com.projectManagement.taskflow.controller;

import com.projectManagement.taskflow.dto.LoginCredentials;
import com.projectManagement.taskflow.enums.RoleEnum;
import com.projectManagement.taskflow.security.JwtUtil;
import com.projectManagement.taskflow.service.AuthService;
import com.projectManagement.taskflow.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    private String login(@RequestBody LoginCredentials loginCredentials){
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

}
//eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJBRElUWUEiLCJ1c2VybmFtZSI6IkFESVRZQSIsInJvbGUiOiJBRE1JTiIsImlhdCI6MTc4MzU5NTkzMiwiZXhwIjoxNzgzNTk5NTMyfQ.IR7gNbrY31EHd6FgjtacdGWExD8AdHvdZ25QCxo0TeI