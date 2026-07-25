package com.projectManagement.taskflow.mapper;

import com.projectManagement.taskflow.dto.ProjectRequestDto;
import com.projectManagement.taskflow.dto.ProjectResponseDto;
import com.projectManagement.taskflow.entity.ProjectEntity;
import com.projectManagement.taskflow.entity.ProjectMember;
import com.projectManagement.taskflow.entity.TaskEntity;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class ProjectMapper {

    public ProjectEntity toEntity(ProjectRequestDto dto){
        ProjectEntity entity = new ProjectEntity();
//        entity.
        return entity;
    }

    public ProjectResponseDto toDto(ProjectEntity entity){
        ProjectResponseDto dto = new ProjectResponseDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setStatus(entity.getStatus());
        dto.setUserId(entity.getUser().getId());
        dto.setTaskIds(entity.getTasks()
                .stream()
                .map(TaskEntity::getId)
                .collect(Collectors.toList())
        );
        dto.setProjectMemberIds(entity.getProjectMembers()
                .stream()
                .map(ProjectMember::getId)
                .collect(Collectors.toList())
        );
        return dto;
    }
}
