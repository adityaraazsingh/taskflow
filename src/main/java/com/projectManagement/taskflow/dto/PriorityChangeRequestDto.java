package com.projectManagement.taskflow.dto;

import com.projectManagement.taskflow.enums.Priority;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PriorityChangeRequestDto {
    @NotNull(message = "priority can't be null")
    private Priority priority;
}
