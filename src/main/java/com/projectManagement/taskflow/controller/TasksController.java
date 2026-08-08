package com.projectManagement.taskflow.controller;

import com.projectManagement.taskflow.dto.*;
import com.projectManagement.taskflow.entity.CommentEntity;
import com.projectManagement.taskflow.entity.TaskEntity;
import com.projectManagement.taskflow.entity.UserEntity;
import com.projectManagement.taskflow.enums.Status;
import com.projectManagement.taskflow.exception.TaskNotFoundException;
import com.projectManagement.taskflow.mapper.TaskMapper;
import com.projectManagement.taskflow.repository.TaskRepo;
import com.projectManagement.taskflow.service.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.config.Task;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tasks")
public class TasksController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private TagService tagService;

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private TaskRepo taskRepo;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @PreAuthorize("hasRole('ADMIN') or project_security.isProjectCreatorFromTaskId(#taskId) or project_security.isProjectMemberFromTaskId(#taskId)")
    @PostMapping("/{taskId}/comments")
    public ResponseEntity<String> postCommentsForTask(@PathVariable Long taskId,
                                                      @Valid @RequestBody List<CommentRequestDTO> comments){
        comments.forEach((comment)-> {
            CommentResponseDto dto = commentService.addComment(taskId, comment);
            messagingTemplate.convertAndSend("/topic/comments", dto);
        });
        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }

    @PostMapping("/project/{projectId}")
    public ResponseEntity<TaskResponseDto> createTask(@Valid @RequestBody TaskRequestDTO dto, @PathVariable Long projectId){
        return ResponseEntity.status(HttpStatus.CREATED).body(taskMapper.toDto(taskService.createTask(projectId,dto)));
    }

    @GetMapping("/{id}")
    public TaskResponseDto getTaskById(@PathVariable Long id){
        return taskService.getTaskById(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponseDto> updateTaskById(@PathVariable Long id, @Valid @RequestBody TaskRequestDTO updatedTask){
        return ResponseEntity.ok(taskService.updateTask(id, updatedTask));
    }

    @PreAuthorize("hasRole('ADMIN') or tasks_security.isAssignedToTask(#taskId)")
    @PatchMapping("/{taskId}/status")
    public ResponseEntity<String> changeStatusOfTask(@PathVariable Long taskId,
                                                     @Valid @RequestBody StatusChangeRequestDto status){
        taskService.updateStatus(taskId, status.getStatus());
        return ResponseEntity.ok(null);
    }

    @PreAuthorize("hasRole('ADMIN') or tasks_security.isAssignedToTask(#taskId) or project_security.isProjectCreatorFromTaskId(#taskId)")
    @PatchMapping("/{taskId}/priority")
    public ResponseEntity<String> changePriorityOfTask(@PathVariable Long taskId,
                                                     @Valid @RequestBody PriorityChangeRequestDto dto){
        taskService.updatePriority(taskId, dto.getPriority());
        return ResponseEntity.ok(null);
    }

    @PreAuthorize("hasRole('ADMIN') or tasks_security.isAssignedToTask(#taskId) or project_security.isProjectCreatorFromTaskId(#taskId)")
    @PatchMapping("/{taskId}/assignee")
    public ResponseEntity<String> changeAssignee(@PathVariable Long taskId,
                                                 @Valid @RequestBody UserRequestDTO user){
        return ResponseEntity.ok(taskService.assignTask(taskId, user));
    }

    @PreAuthorize("hasRole('ADMIN') or tasks_security.isAssignedToTask(#taskId) or project_security.isProjectCreatorFromTaskId(#taskId)")
    @DeleteMapping("/{taskId}")
    public ResponseEntity<String> deleteTask(@PathVariable Long taskId){
        return ResponseEntity.ok(taskService.deleteTask(taskId));
    }

    @GetMapping("/{id}/comments")
    public Page<CommentResponseDto> getComments(@PathVariable Long id,
                                                @RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "10") int size){
        Pageable pageable = PageRequest.of(page,size);
        return commentService.listCommentsForTask(id, pageable);
    }



    @PostMapping("/{id}/tags/{tagId}")
    private ResponseEntity<String> addTasksPerTags(@PathVariable Long id, @PathVariable Long tagId) {
        tagService.AttachTagToTask(id, tagId);
        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }

    @GetMapping("/{id}/tags")
    private ResponseEntity<List<TagResponseDto>> getTagsOnATask(@PathVariable Long id){
        TaskEntity task = taskRepo.findById(id).orElseThrow(()->new TaskNotFoundException("Task Not found"));
        List<TagResponseDto> tags = task.getTags().stream().map((tag)->tagService.getTagById(tag.getId())).collect(Collectors.toList());
        return ResponseEntity.ok(tags);
    }

    @DeleteMapping("/{id}/tags/{tagId}")
    private ResponseEntity<String> deleteTagForTask(@PathVariable Long id, @PathVariable Long tagId){
        tagService.removeTagFromTask(id, tagId);
        return ResponseEntity.noContent().build();
    }
}
