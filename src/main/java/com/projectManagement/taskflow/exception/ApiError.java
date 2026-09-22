package com.projectManagement.taskflow.exception;

import java.time.OffsetDateTime;
import java.util.Map;

public record ApiError(
        String type,         // machine id or URI (e.g. "urn:taskflow:validation")
        String title,        // short human title
        int status,          // HTTP status code
        String detail,       // human-friendly explanation
        String instance,     // request path or id
        String traceId,      // correlation id (X-Request-Id)
        String tenantId,     // tenant id (if available)
        String errorCode,    // application-specific error code
        OffsetDateTime timestamp,
        Map<String, Object> extras
) {}
