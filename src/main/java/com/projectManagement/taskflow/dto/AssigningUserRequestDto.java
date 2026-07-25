package com.projectManagement.taskflow.dto;

import com.projectManagement.taskflow.enums.RoleInProject;

public class AssigningUserRequestDto {
    private RoleInProject roleInProject;
    private Long userId;

    public RoleInProject getRoleInProject() {
        return roleInProject;
    }

    public void setRoleInProject(RoleInProject roleInProject) {
        this.roleInProject = roleInProject;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
