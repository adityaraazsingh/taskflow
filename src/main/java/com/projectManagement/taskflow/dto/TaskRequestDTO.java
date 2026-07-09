package com.projectManagement.taskflow.dto;

import com.projectManagement.taskflow.enums.priority;

import java.util.Date;

public class TaskRequestDTO {
    private String title;
    private String description;
    private Date dueDate;
    private priority priority;

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
