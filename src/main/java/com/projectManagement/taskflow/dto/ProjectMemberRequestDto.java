package com.projectManagement.taskflow.dto;

import com.projectManagement.taskflow.enums.RoleInProject;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProjectMemberRequestDto {
    @NotNull
    private Long projectId;
    @NotNull
    private Long userId;
    private RoleInProject roleInProject;
}
