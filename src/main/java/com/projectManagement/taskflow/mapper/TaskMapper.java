package com.projectManagement.taskflow.mapper;

import com.projectManagement.taskflow.dto.TaskRequestDTO;
import com.projectManagement.taskflow.dto.TaskResponseDto;
import com.projectManagement.taskflow.entity.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TaskMapper {
    public TaskEntity toEntity(TaskRequestDTO taskDTO, ProjectEntity project, UserEntity user){
        TaskEntity task = new TaskEntity();

        task.setAssignee(user);
        task.setProject(project);
        task.setTitle(taskDTO.getTitle());
        task.setStatus(taskDTO.getStatus());
        task.setDueDate(taskDTO.getDueDate());
        task.setPriority(taskDTO.getPriority());
        task.setDescription(taskDTO.getDescription());
        // Not saving comments and Tags

        return task;
    }

    public TaskResponseDto toDto(TaskEntity entity){
        TaskResponseDto dto = new TaskResponseDto();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setDescription(entity.getDescription());
        dto.setAssigneeId(entity.getAssignee().getId());
        dto.setStatus(entity.getStatus());
        dto.setPriority(entity.getPriority());
        dto.setDescription(entity.getDescription());
        dto.setDueDate(entity.getDueDate());
        dto.setCommentIds(entity.getComments().stream().map(CommentEntity::getId).collect(Collectors.toList()));
        dto.setTagIds(entity.getTags().stream().map(TagEntity::getId).collect(Collectors.toList()));
        return dto;
    }
}
