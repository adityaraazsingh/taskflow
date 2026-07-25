package com.projectManagement.taskflow.dto;
import com.projectManagement.taskflow.enums.RoleEnum;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UserResponseDto {
    private Long id;
    private String username;
    private String email;
    private RoleEnum role;
    private List<Long> projectIds = new ArrayList<>();
    private List<Long> taskIds = new ArrayList<>();
    private List<Long> commentIds = new ArrayList<>();
    private List<Long> projectMemberIds = new ArrayList<>();
    private Date createdAt;

    public List<Long> getProjectIds() {
        return projectIds;
    }

    public void setProjectIds(List<Long> projectIds) {
        this.projectIds = projectIds;
    }

    public List<Long> getTaskIds() {
        return taskIds;
    }

    public void setTaskIds(List<Long> taskIds) {
        this.taskIds = taskIds;
    }

    public List<Long> getCommentIds() {
        return commentIds;
    }

    public void setCommentIds(List<Long> commentIds) {
        this.commentIds = commentIds;
    }

    public List<Long> getProjectMemberIds() {
        return projectMemberIds;
    }

    public void setProjectMemberIds(List<Long> projectMemberIds) {
        this.projectMemberIds = projectMemberIds;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public RoleEnum getRole() {
        return role;
    }

    public void setRole(RoleEnum role) {
        this.role = role;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
