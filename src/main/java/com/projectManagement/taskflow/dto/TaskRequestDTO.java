package com.projectManagement.taskflow.dto;

import com.projectManagement.taskflow.enums.Status;
import com.projectManagement.taskflow.enums.Priority;

import java.util.Date;

public class TaskRequestDTO {
    private String title;
    private String description;
    private Date dueDate;
    private Priority priority;
    private Status status;

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
