package com.projectManagement.taskflow.controller;

import com.projectManagement.taskflow.dto.TagRequestDTO;
import com.projectManagement.taskflow.dto.TagResponseDto;
import com.projectManagement.taskflow.mapper.TagMapper;
import com.projectManagement.taskflow.service.TagService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/tags")
public class TagsController {

    private final TagService tagService;
    private final TagMapper tagMapper;

    public TagsController(TagService tagService, TagMapper tagMapper) {
        this.tagService = tagService;
        this.tagMapper = tagMapper;
    }

    @GetMapping
    public List<TagResponseDto> getTags(){
        return tagService.getAllTags();
    }

    //TODO: however it needs improvement;
    @PostMapping
    public List<TagResponseDto> postTags(@Valid @RequestBody List<TagRequestDTO> tags){
        return tagService.postTags(tags);
    }

}
