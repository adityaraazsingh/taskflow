package com.projectManagement.taskflow.dto;

import com.projectManagement.taskflow.enums.Status;
import com.projectManagement.taskflow.enums.Priority;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;

@Data
public class TaskRequestDTO {
    @NotNull
    private String title;
    @NotNull
    private String description;
    private Date dueDate;
    private Priority priority;
    private Status status;

}
