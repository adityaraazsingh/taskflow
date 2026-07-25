package com.projectManagement.taskflow.dto;

import com.projectManagement.taskflow.enums.RoleInProject;

public class ProjectMemberResponseDto {
    private Long id;
    private Long projectId;
    private UserResponseDto user;
    private RoleInProject roleInProject;

    public UserResponseDto getUser() {
        return user;
    }

    public void setUser(UserResponseDto user) {
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public RoleInProject getRoleInProject() {
        return roleInProject;
    }

    public void setRoleInProject(RoleInProject roleInProject) {
        this.roleInProject = roleInProject;
    }
}
