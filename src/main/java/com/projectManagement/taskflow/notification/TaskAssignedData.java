package com.projectManagement.taskflow.notification;

public record TaskAssignedData(
        Long taskId,
        String taskTitle,
        Long assigneeId,
        String assigneeName
){}
