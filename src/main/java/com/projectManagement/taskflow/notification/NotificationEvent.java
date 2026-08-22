package com.projectManagement.taskflow.notification;

import java.util.Map;

public record NotificationEvent(
        NotificationEventEnum eventType,
        String tenantSchema,
        String message,
        Map<String, Object> data
) {}