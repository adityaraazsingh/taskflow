package com.projectManagement.taskflow.notification;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;

public record NotificationEvent(
        NotificationEventEnum eventType,
        String tenantSchema,
        String message,
        Map<String, Object> data,
        Date createdAt
) {}