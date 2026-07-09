package com.projectManagement.taskflow.controller;

import com.projectManagement.taskflow.dto.CommentRequestDTO;
import com.projectManagement.taskflow.dto.TagRequestDTO;
import com.projectManagement.taskflow.dto.TaskRequestDTO;
import com.projectManagement.taskflow.entity.CommentEntity;
import com.projectManagement.taskflow.entity.TaskEntity;
import com.projectManagement.taskflow.entity.UserEntity;
import com.projectManagement.taskflow.enums.Status;
import com.projectManagement.taskflow.repository.TaskRepo;
import com.projectManagement.taskflow.service.CommentService;
import com.projectManagement.taskflow.service.TagService;
import com.projectManagement.taskflow.service.TaskService;
import com.projectManagement.taskflow.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @GetMapping("/{id}")
    public TaskEntity getTaskById(@PathVariable Long id){
        UserEntity user = userService.findByUsername("User");
        return taskService.getTaskById(id,user);
    }

    @PutMapping("/{id}")
    public TaskEntity updateTaskById(@PathVariable Long id, @RequestBody TaskEntity updatedTask){
        UserEntity user = userService.findByUsername("User");
        return taskService.updateTask(id, updatedTask,user );
    }

    // "IN_PROGRESS" just this for change
    @PatchMapping("/{id}/status")
    public String changeStatusOfTask(@PathVariable Long id, @RequestBody Status status){
        UserEntity user = userService.findByUsername("User");
        taskService.updateStatus(id, status, user);
        return "Status Changed easily";
    }

    @PatchMapping("/{id}/assignee")
    public String changeAssignee(@PathVariable Long id,@RequestBody UserEntity user){
        return "Assignee changed Successfully, "+taskService.assignTask(id, user);
    }

    @DeleteMapping("/{id}")
    public String deleteTask(@PathVariable Long id){
        taskService.deleteTask(id);
        return "Task deleted succesfully";
    }

    @GetMapping("/{id}/comments")
    public List<CommentEntity> getComments(@PathVariable Long id){
        return commentService.listCommentForTask(id);
    }

    //TODO: Add a logic to add users here
    @PostMapping("/{id}/comments")
    public String postCommentsForTask(@PathVariable Long id, @RequestBody List<CommentRequestDTO> comments){
        UserEntity user = userService.findByUsername("User");
        comments.forEach((comment)-> commentService.addComment(id,comment,user));
        return "Added comments for task";
    }

    @PostMapping("{id}/tags/{tagId}")
    private String addTasksPerTags(@PathVariable Long id, @PathVariable Long tagId){
        tagService.AttachTagToTask(id, tagId);
        return "Add task Per Tag";
    }

    @DeleteMapping("{id}/tags/{tagId}")
    private String deleteTagForTask(@PathVariable Long id, @PathVariable Long tagId){
        tagService.removeTagFromTask(id, tagId);
        return "Tag removed from the task Successfully";
    }

}
