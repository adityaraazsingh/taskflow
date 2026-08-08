package com.projectManagement.taskflow.dto;

import com.projectManagement.taskflow.enums.RoleEnum;
import lombok.Data;

@Data
public class LoginCredentials {
    private String username;
    private String password;
}
