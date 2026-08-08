package com.projectManagement.taskflow.dto;

import com.projectManagement.taskflow.enums.Priority;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PriorityChangeRequestDto {
    @NotNull
    private Priority priority;
}
