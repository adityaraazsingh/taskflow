package com.projectManagement.taskflow.mapper;

import com.projectManagement.taskflow.dto.TagRequestDTO;
import com.projectManagement.taskflow.dto.TagResponseDto;
import com.projectManagement.taskflow.entity.TagEntity;
import com.projectManagement.taskflow.entity.TaskEntity;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class TagMapper {
    public TagEntity toEntity(TagRequestDTO dto){
        TagEntity entity = new TagEntity();
        entity.setName(dto.getName());
        entity.setColorHex(dto.getColorHex());
        return entity;
    }

    public TagResponseDto toDto(TagEntity entity){
        TagResponseDto dto = new TagResponseDto();
        dto.setId(entity.getId());
        dto.setTaskIds(entity.getTasks()
                .stream()
                .map(TaskEntity::getId)
                .collect(Collectors.toList()));
        dto.setName(entity.getName());
        dto.setColorHex(entity.getColorHex());
        return dto;
    }
}
