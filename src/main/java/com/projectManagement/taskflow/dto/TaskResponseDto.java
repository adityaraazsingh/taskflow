package com.projectManagement.taskflow.dto;
import com.projectManagement.taskflow.enums.Status;
import com.projectManagement.taskflow.enums.Priority;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TaskResponseDto {
    private Long id;
    private String title;
    private String description;
    private Long assigneeId;
    private Date dueDate;
    private Priority priority;
    private Status status;
    private Long projectId;
    private List<Long> commentIds = new ArrayList<>();
    private List<Long> tagIds = new ArrayList<>();

    public Long getProjectId() { return projectId; }

    public void setProjectId(Long projectId) { this.projectId = projectId; }

    public Long getAssigneeId() {
        return assigneeId;
    }

    public void setAssigneeId(Long assigneeId) {
        this.assigneeId = assigneeId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Long> getCommentIds() {
        return commentIds;
    }

    public void setCommentIds(List<Long> commentId) {
        this.commentIds = commentId;
    }

    public List<Long> getTagIds() {
        return tagIds;
    }

    public void setTagIds(List<Long> tagsId) {
        this.tagIds = tagsId;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }
}
