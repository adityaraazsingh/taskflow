package com.projectManagement.taskflow.dto;

import com.projectManagement.taskflow.enums.Status;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ProjectRequestDto {
    @NotNull
    private String name;
    private String description;
    @NotNull
    private Status status;
    private List<Long> taskIds = new ArrayList<>();
    private List<Long> projectMemberIds = new ArrayList<>();
}

