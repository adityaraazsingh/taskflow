package com.projectManagement.taskflow.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.projectManagement.taskflow.entity.ProjectMember;
import com.projectManagement.taskflow.entity.TaskEntity;
import com.projectManagement.taskflow.entity.UserEntity;
import com.projectManagement.taskflow.enums.Status;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ProjectResponseDto {
    private Long id;
    private String name;
    private String description;
    private Status status;
    private Long userId;
    private List<Long> taskIds = new ArrayList<>();
    private List<Long> projectMemberIds = new ArrayList<>();
}
