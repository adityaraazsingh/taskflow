package com.projectManagement.taskflow.dto;

import com.projectManagement.taskflow.enums.Status;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ProjectRequestDto {
    @NotNull(message = "name can't be null")
    private String name;
    private String description;
    @NotNull(message = "status can't be null")
    private Status status;
    private List<Long> taskIds = new ArrayList<>();
    private List<Long> projectMemberIds = new ArrayList<>();
}

