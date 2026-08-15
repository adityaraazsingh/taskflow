package com.projectManagement.taskflow.mapper;

import com.projectManagement.taskflow.dto.PageResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class PageMapper {

    public <T> PageResponseDto<T> toDto(Page<T> page) {

        PageResponseDto<T> dto = new PageResponseDto<>();

        dto.setContent(page.getContent());
        dto.setPage(page.getNumber());
        dto.setSize(page.getSize());
        dto.setTotalElements(page.getTotalElements());
        dto.setTotalPages(page.getTotalPages());

        return dto;
    }
}
