package com.projectManagement.taskflow.dto;

import com.projectManagement.taskflow.enums.Status;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StatusChangeRequestDto {
    @NotNull(message = "status can't be null")
    private Status status;
}
