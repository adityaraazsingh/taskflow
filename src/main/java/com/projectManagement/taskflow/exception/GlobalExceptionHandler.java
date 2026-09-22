package com.projectManagement.taskflow.exception;

import jakarta.validation.ValidationException;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private ApiError buildError(
            HttpStatus status,
            String title,
            String detail,
            ErrorCode errorCode,
            Map<String, Object> extras
    ) {
        return new ApiError(
                "urn:taskflow:" + errorCode.name().toLowerCase(),
                title,
                status.value(),
                detail,
                MDC.get("instance"),
                MDC.get("traceId"),
                MDC.get("tenantId"),
                errorCode.name(),
                OffsetDateTime.now(),
                extras
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> methodArgumentNotValidException(MethodArgumentNotValidException ex){
        Map<String, Object> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(err ->
                        fieldErrors.put(err.getField(),err.getDefaultMessage())
                );

        ApiError error = buildError(
                HttpStatus.BAD_REQUEST,
                "Validation Failed",
                "One or more fields are invalid",
                ErrorCode.VALIDATION_ERROR,
                Map.of("fieldErrors", fieldErrors)
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgument(IllegalArgumentException ex){
        ApiError error = buildError(
                HttpStatus.BAD_REQUEST,
                "Invalid Argument",
                ex.getMessage(),
                ErrorCode.INVALID_ARGUMENT,
                null
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<ApiError> handleTaskNotFound(TaskNotFoundException ex) {
        ApiError apiError = buildError(
                HttpStatus.NOT_FOUND,
                "Task not found",
                ex.getMessage(),
                ErrorCode.TASK_NOT_FOUND,
                null
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiError);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiError> handleUserNotFound(UserNotFoundException ex) {
        ApiError apiError = buildError(
                HttpStatus.NOT_FOUND,
                "User not found",
                ex.getMessage(),
                ErrorCode.USER_NOT_FOUND,
                null
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiError);
    }

    @ExceptionHandler(ProjectNotFoundException.class)
    public ResponseEntity<ApiError> handleProjectNotFound(ProjectNotFoundException ex) {
        ApiError apiError = buildError(
                HttpStatus.NOT_FOUND,
                "Project not found",
                ex.getMessage(),
                ErrorCode.PROJECT_NOT_FOUND,
                null
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiError);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDenied(AccessDeniedException ex) {
        ApiError apiError = buildError(
                HttpStatus.UNAUTHORIZED,
                "Access Denied",
                ex.getMessage(),
                ErrorCode.ACCESS_DENIED,
                null
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiError);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiError> handleInvalidCredentials(InvalidCredentialsException ex){
        ApiError apiError = buildError(
                HttpStatus.BAD_REQUEST,
                "Invalid Credentials",
                ex.getMessage(),
                ErrorCode.INVALID_CREDENTIALS,
                null
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiError);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(Exception ex){

        ApiError error = buildError(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal Server Error",
                ex.getMessage(),
                ErrorCode.INTERNAL_ERROR,
                null
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
