package com.projectManagement.taskflow.controller;

import com.projectManagement.taskflow.entity.TagEntity;
import com.projectManagement.taskflow.entity.TaskEntity;
import com.projectManagement.taskflow.repository.TagRepo;
import com.projectManagement.taskflow.service.TagService;
import com.projectManagement.taskflow.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.awt.print.Pageable;
import java.util.List;

@RestController
@RequestMapping("/api/tags")
public class TagsController {

    @Autowired
    private TagService tagService;

    @Autowired
    private TagRepo tagRepo;

    @Autowired
    private TaskService taskService;

    @GetMapping
    private List<TagEntity> getTags(){
        return tagRepo.findAll();
    }

    //TODO: however it needs improvement;
    @PostMapping
    private List<TagEntity> postTags(@RequestBody List<TagEntity> tags){
        return tagRepo.saveAll(tags);
    }

}
