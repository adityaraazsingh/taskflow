package com.projectManagement.taskflow.dto;

import com.projectManagement.taskflow.enums.RoleEnum;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class UserRequestDTO {
    @NotNull
    private String username;
    private String password;
    @NotNull
    private RoleEnum role;
    @Email
    private String email;
}
