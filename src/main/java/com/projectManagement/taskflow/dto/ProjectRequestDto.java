package com.projectManagement.taskflow.dto;

import com.projectManagement.taskflow.enums.Status;
import java.util.ArrayList;
import java.util.List;

public class ProjectRequestDto {
    private String name;
    private String description;
    private Status status;
//    private Long userId;
    private List<Long> taskIds = new ArrayList<>();
    private List<Long> projectMemberIds = new ArrayList<>();

//    public Long getUserId() {
//        return userId;
//    }
//
//    public void setUserId(Long userId) {
//        this.userId = userId;
//    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public List<Long> getTaskIds() {
        return taskIds;
    }

    public void setTaskIds(List<Long> taskIds) {
        this.taskIds = taskIds;
    }

    public List<Long> getProjectMemberIds() {
        return projectMemberIds;
    }

    public void setProjectMemberIds(List<Long> projectMemberIds) {
        this.projectMemberIds = projectMemberIds;
    }
}

