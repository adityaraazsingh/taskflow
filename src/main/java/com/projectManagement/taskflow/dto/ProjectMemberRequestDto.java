package com.projectManagement.taskflow.dto;

import com.projectManagement.taskflow.enums.RoleInProject;

public class ProjectMemberRequestDto {
    private Long projectId;
    private Long userId;
    private RoleInProject roleInProject;

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public RoleInProject getRoleInProject() {
        return roleInProject;
    }

    public void setRoleInProject(RoleInProject roleInProject) {
        this.roleInProject = roleInProject;
    }
}
