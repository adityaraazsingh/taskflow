package com.projectManagement.taskflow.dto;

import com.projectManagement.taskflow.enums.RoleInProject;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProjectMemberRequestDto {
    @NotNull(message = "projectId is null")
    private Long projectId;
    @NotNull(message = "userId is null")
    private Long userId;
    private RoleInProject roleInProject;
}
