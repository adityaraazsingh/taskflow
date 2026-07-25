package com.projectManagement.taskflow.mapper;

import com.projectManagement.taskflow.dto.UserRequestDTO;
import com.projectManagement.taskflow.dto.UserResponseDto;
import com.projectManagement.taskflow.entity.*;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class UserMapper {

    public UserEntity toEntity(UserRequestDTO dto, String encodedPassword) {
        UserEntity user = new UserEntity();

        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setRole(dto.getRole());
        user.setPasswordHash(encodedPassword);
        user.setCreatedAt(new java.util.Date());

        return user;
    }

    public UserResponseDto toDto(UserEntity entity){
        UserResponseDto dto = new UserResponseDto();
        dto.setId(entity.getId());
        dto.setUsername(entity.getUsername());
        dto.setEmail(entity.getEmail());
        dto.setRole(entity.getRole());
        dto.setProjectIds(entity.getProjects()
                .stream()
                .map(ProjectEntity::getId)
                .collect(Collectors.toList()));
        dto.setTaskIds(entity.getTasks()
                .stream()
                .map(TaskEntity::getId)
                .collect(Collectors.toList()));
        dto.setCommentIds(entity.getComment()
                .stream()
                .map(CommentEntity::getId)
                .collect(Collectors.toList()));
        dto.setProjectMemberIds(entity.getProjectMembers()
                .stream()
                .map(ProjectMember::getId)
                .collect(Collectors.toList()));
        dto.setCreatedAt(entity.getCreatedAt());
        return dto;
    }
}
