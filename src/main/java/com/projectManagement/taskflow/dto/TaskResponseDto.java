package com.projectManagement.taskflow.dto;
import com.projectManagement.taskflow.enums.Status;
import com.projectManagement.taskflow.enums.Priority;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class TaskResponseDto {
    private Long id;
    private String title;
    private String description;
    private Long assigneeId;
    private Date dueDate;
    private Priority priority;
    private Status status;
    private Long projectId;
    private List<Long> commentIds = new ArrayList<>();
    private List<Long> tagIds = new ArrayList<>();
}
