package com.projectManagement.taskflow.dto;

import com.projectManagement.taskflow.enums.RoleInProject;
import lombok.Data;

@Data
public class ProjectMemberResponseDto {
    private Long id;
    private Long projectId;
    private UserResponseDto user;
    private RoleInProject roleInProject;
}
