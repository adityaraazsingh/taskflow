package com.projectManagement.taskflow.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChangePasswordRequestDto {
    @NotNull
    private String currentPassword;
    @NotNull
    private String newPassword;
}
