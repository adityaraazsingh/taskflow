package com.projectManagement.taskflow.controller;

import com.projectManagement.taskflow.dto.CommentRequestDTO;
import com.projectManagement.taskflow.dto.TagRequestDTO;
import com.projectManagement.taskflow.dto.TaskRequestDTO;
import com.projectManagement.taskflow.entity.CommentEntity;
import com.projectManagement.taskflow.entity.TaskEntity;
import com.projectManagement.taskflow.entity.UserEntity;
import com.projectManagement.taskflow.enums.Status;
import com.projectManagement.taskflow.repository.TaskRepo;
import com.projectManagement.taskflow.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.config.Task;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TasksController {

    @Autowired
    private TaskRepo taskRepo;

    @Autowired
    private TaskService taskService;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private TagService tagService;

    @Autowired
    private AuthService authService;

    @GetMapping("/{id}")
    public TaskEntity getTaskById(@PathVariable Long id){
        return taskService.getTaskById(id);
    }

    @PutMapping("/{id}")
    public TaskEntity updateTaskById(@PathVariable Long id, @RequestBody TaskEntity updatedTask){
        UserEntity user = authService.getCurrentUser();
        return taskService.updateTask(id, updatedTask,user );
    }

    // "IN_PROGRESS" just this for change
    @PatchMapping("/{id}/status")
    public String changeStatusOfTask(@PathVariable Long id, @RequestBody Status status){
        UserEntity user = authService.getCurrentUser();
        return taskService.updateStatus(id, status, user);
    }

    @PatchMapping("/{id}/assignee")
    public String changeAssignee(@PathVariable Long id,@RequestBody UserEntity user){
        return taskService.assignTask(id, user);
    }

    @DeleteMapping("/{id}")
    public String deleteTask(@PathVariable Long id){
        return taskService.deleteTask(id);
    }

    @GetMapping("/{id}/comments")
    public Page<CommentEntity> getComments(@PathVariable Long id,
                                           @RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "10") int size){
        Pageable pageable = PageRequest.of(page,size);
        return commentService.listCommentsForTask(id, pageable);
    }

    //TODO: Add a logic to add users here
    @PostMapping("/{id}/comments")
    public String postCommentsForTask(@PathVariable Long id, @RequestBody List<CommentRequestDTO> comments){
        UserEntity user = authService.getCurrentUser();
        comments.forEach((comment)-> commentService.addComment(id,comment,user));
        return "Added comments for task";
    }

    @PostMapping("{id}/tags/{tagId}")
    private String addTasksPerTags(@PathVariable Long id, @PathVariable Long tagId){
        return tagService.AttachTagToTask(id, tagId);
    }

    @DeleteMapping("{id}/tags/{tagId}")
    private String deleteTagForTask(@PathVariable Long id, @PathVariable Long tagId){
        return tagService.removeTagFromTask(id, tagId);
    }

}
