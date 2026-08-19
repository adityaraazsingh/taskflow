package com.projectManagement.taskflow.notification;

public record NotificationEvent(
        NotificationEventEnum eventType,
        String tenantSchema,
        String message
) {}