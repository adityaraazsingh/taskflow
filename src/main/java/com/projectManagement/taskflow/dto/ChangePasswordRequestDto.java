package com.projectManagement.taskflow.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChangePasswordRequestDto {
    @NotNull(message = "current Password can't be null")
    private String currentPassword;
    @NotNull(message = "New password can't be null")
    private String newPassword;
}
