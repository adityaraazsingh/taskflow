package com.projectManagement.taskflow.dto;
import com.projectManagement.taskflow.enums.Status;
import com.projectManagement.taskflow.enums.priority;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TaskResponseDto {
    private Long id;
    private String title;
    private String description;
    private Date dueDate;
    private priority priority;
    private Status status;
    private List<Long> commentIds = new ArrayList<>();;
    private List<Long> tagIds = new ArrayList<>();;

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

    public priority getPriority() {
        return priority;
    }

    public void setPriority(priority priority) {
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
