package com.projectManagement.taskflow.dto;

import com.projectManagement.taskflow.enums.RoleEnum;
import com.projectManagement.taskflow.enums.Status;
import lombok.Data;

@Data
public class AuthResponse {
    private String username;
    private String accessToken;
    private String refreshToken;
    private RoleEnum role;
}
