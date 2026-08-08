package com.projectManagement.taskflow.dto;

import com.projectManagement.taskflow.enums.RoleInProject;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AssigningUserRequestDto {
    @NotNull
    private RoleInProject roleInProject;
    @NotNull
    private Long userId;
}
