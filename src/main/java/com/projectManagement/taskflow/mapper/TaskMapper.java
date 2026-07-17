package com.projectManagement.taskflow.mapper;

import com.projectManagement.taskflow.dto.TaskRequestDTO;
import com.projectManagement.taskflow.entity.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TaskMapper {
    public TaskEntity toEntity(TaskRequestDTO taskDTO, ProjectEntity project, UserEntity user){
        TaskEntity task = new TaskEntity();

        task.setProject(project);

        task.setDescription(taskDTO.getDescription());
        task.setTitle(taskDTO.getTitle());
        task.setDueDate(taskDTO.getDueDate());
        task.setAssignee(user);
        // Not saving comments and Tags
        task.setStatus(taskDTO.getStatus());
        task.setPriority(taskDTO.getPriority());

        return task;
    }

    public TaskRequestDTO toDto(TaskEntity entity){
        TaskRequestDTO dto = new TaskRequestDTO();
        dto.setDescription(entity.getDescription());
        dto.setStatus(entity.getStatus());
        dto.setPriority(entity.getPriority());
        dto.setDescription(entity.getDescription());
        dto.setDueDate(entity.getDueDate());
        return dto;
    }
}
