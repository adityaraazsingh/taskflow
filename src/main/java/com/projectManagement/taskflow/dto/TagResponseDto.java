package com.projectManagement.taskflow.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.projectManagement.taskflow.entity.TaskEntity;
import jakarta.persistence.ManyToMany;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TagResponseDto {
    private Long id;
    private List<Long> taskIds = new ArrayList<>();
    private String name;
    private String colorHex;
}
