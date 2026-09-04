package com.projectManagement.taskflow.dto;

import com.projectManagement.taskflow.enums.RoleEnum;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TenantUserRequestDto {
    private String tenantName;
    @NotNull(message = "username can't be null")
    private String username;
    private String password;
    @NotNull(message = "role can't be null")
    private RoleEnum role;
    @Email
    private String email;
}
