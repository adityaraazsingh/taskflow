package com.projectManagement.taskflow.controller;

import com.projectManagement.taskflow.repository.CommentRepo;
import com.projectManagement.taskflow.service.CommentService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/comments")
@RestController
public class CommentsController {

    @Autowired
    private CommentRepo commentRepo;

    @Autowired
    private CommentService commentService;

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteComments(@PathVariable Long id){
        return ResponseEntity.noContent().build();
    }
}
