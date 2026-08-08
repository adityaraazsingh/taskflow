package com.projectManagement.taskflow.dto;

import com.projectManagement.taskflow.enums.Status;
import lombok.Data;

@Data
public class StatusChangeRequestDto {
    private Status status;
}
