package com.projectManagement.taskflow.controller;

import com.projectManagement.taskflow.dto.*;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.config.Task;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TasksController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private TagService tagService;

    @GetMapping("/{id}")
    public TaskResponseDto getTaskById(@PathVariable Long id){
        return taskService.getTaskById(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponseDto> updateTaskById(@PathVariable Long id, @RequestBody TaskRequestDTO updatedTask){
        return ResponseEntity.ok(taskService.updateTask(id, updatedTask));
    }

    // "IN_PROGRESS" just this for change
    @PatchMapping("/{id}/status")
    public ResponseEntity<String> changeStatusOfTask(@PathVariable Long id,
                                                     @RequestBody Status status){
        return ResponseEntity.ok(taskService.updateStatus(id, status));
    }

    @PatchMapping("/{id}/assignee")
    public ResponseEntity<String> changeAssignee(@PathVariable Long id,
                                                 @RequestBody UserRequestDTO user){
        return ResponseEntity.ok(taskService.assignTask(id, user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTask(@PathVariable Long id){
        return ResponseEntity.ok(taskService.deleteTask(id));
    }

    @GetMapping("/{id}/comments")
    public Page<CommentResponseDto> getComments(@PathVariable Long id,
                                                @RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "10") int size){
        Pageable pageable = PageRequest.of(page,size);
        return commentService.listCommentsForTask(id, pageable);
    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<String> postCommentsForTask(@PathVariable Long id,
                                                      @RequestBody List<CommentRequestDTO> comments){
        comments.forEach((comment)-> commentService.addComment(id,comment));
        return ResponseEntity.ok("Added comments for task");
    }

    @PostMapping("/{id}/tags/{tagId}")
    private ResponseEntity<String> addTasksPerTags(@PathVariable Long id, @PathVariable Long tagId){
        return ResponseEntity.ok(tagService.AttachTagToTask(id, tagId));
    }

    @DeleteMapping("/{id}/tags/{tagId}")
    private ResponseEntity<String> deleteTagForTask(@PathVariable Long id, @PathVariable Long tagId){
        return ResponseEntity.ok(tagService.removeTagFromTask(id, tagId));
    }
}
