package com.projectManagement.taskflow.controller;

import com.projectManagement.taskflow.dto.TagRequestDTO;
import com.projectManagement.taskflow.dto.TagResponseDto;
import com.projectManagement.taskflow.entity.TagEntity;
import com.projectManagement.taskflow.entity.TaskEntity;
import com.projectManagement.taskflow.mapper.TagMapper;
import com.projectManagement.taskflow.repository.TagRepo;
import com.projectManagement.taskflow.service.TagService;
import com.projectManagement.taskflow.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.awt.print.Pageable;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tags")
public class TagsController {

    @Autowired
    private TagService tagService;

    @Autowired
    private TagRepo tagRepo;

    @Autowired
    private TaskService taskService;

    @Autowired
    private TagMapper tagMapper;

    @GetMapping
    private List<TagResponseDto> getTags(){
        return tagRepo.findAll().stream().map(tagMapper::toDto).collect(Collectors.toList());
    }

    //TODO: however it needs improvement;
    @PostMapping
    private List<TagResponseDto> postTags(@RequestBody List<TagRequestDTO> tags){
        return tagRepo.saveAll(
                tags.stream().map(tagMapper::toEntity)
                        .collect(Collectors.toList()))
                .stream()
                .map(tagMapper::toDto)
                .collect(Collectors.toList());
    }

}
