package com.projectManagement.taskflow.controller;

import com.projectManagement.taskflow.service.CommentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/comments")
@RestController
public class CommentsController {

    private final CommentService commentService;

    public CommentsController(CommentService commentService) {
        this.commentService = commentService;
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @comment_security.isOwner(#id)")
    public ResponseEntity<String> deleteComments(@PathVariable Long id){
        commentService.deleteComment(id);
        return ResponseEntity.noContent().build();
    }
}
