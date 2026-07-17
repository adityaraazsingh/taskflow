package com.projectManagement.taskflow.mapper;

import com.projectManagement.taskflow.dto.TagRequestDTO;
import com.projectManagement.taskflow.entity.TagEntity;
import org.springframework.stereotype.Component;

@Component
public class TagMapper {
    public TagEntity toEntity(TagRequestDTO dto){
        TagEntity entity = new TagEntity();
        entity.setName(dto.getName());
        entity.setColorHex(dto.getColorHex());
        return entity;
    }
}
