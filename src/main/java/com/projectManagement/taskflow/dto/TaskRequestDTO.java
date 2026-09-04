package com.projectManagement.taskflow.dto;

import com.projectManagement.taskflow.enums.Status;
import com.projectManagement.taskflow.enums.Priority;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;

@Data
public class TaskRequestDTO {
    @NotNull(message = "title can't be null")
    private String title;
    @NotNull(message = "Description can't be null")
    private String description;
    private Date dueDate;
    private Priority priority;
    private Status status;

}
