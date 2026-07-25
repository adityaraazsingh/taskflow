package com.projectManagement.taskflow.mapper;

import com.projectManagement.taskflow.dto.CommentRequestDTO;
import com.projectManagement.taskflow.dto.CommentResponseDto;
import com.projectManagement.taskflow.dto.TaskRequestDTO;
import com.projectManagement.taskflow.entity.CommentEntity;
import com.projectManagement.taskflow.entity.TaskEntity;
import com.projectManagement.taskflow.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class CommentMapper {
    public CommentResponseDto toDto(CommentEntity entity){
        CommentResponseDto dto = new CommentResponseDto();
        dto.setId(entity.getId());
        dto.setContent(entity.getContent());
        dto.setName(entity.getName());
        dto.setTaskId(entity.getTask().getId());
        dto.setUserId(entity.getCommentator().getId());
        return dto;
    }

    public CommentEntity toEntity(CommentRequestDTO dto, UserEntity author, TaskEntity task){
        CommentEntity entity = new CommentEntity();
        entity.setName(dto.getName());
        entity.setContent(dto.getContent());
        entity.setCommentator(author);
        entity.setTask(task);
        return entity;
    }
}
