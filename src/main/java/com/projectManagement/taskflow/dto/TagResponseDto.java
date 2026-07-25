package com.projectManagement.taskflow.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.projectManagement.taskflow.entity.TaskEntity;
import jakarta.persistence.ManyToMany;

import java.util.ArrayList;
import java.util.List;

public class TagResponseDto {
    private Long id;
    private List<Long> taskIds = new ArrayList<>();
    private String name;
    private String colorHex;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Long> getTaskIds() {
        return taskIds;
    }

    public void setTaskIds(List<Long> taskIds) {
        this.taskIds = taskIds;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColorHex() {
        return colorHex;
    }

    public void setColorHex(String colorHex) {
        this.colorHex = colorHex;
    }
}
