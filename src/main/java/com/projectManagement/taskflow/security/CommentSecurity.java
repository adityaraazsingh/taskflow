package com.projectManagement.taskflow.security;

import com.projectManagement.taskflow.entity.CommentEntity;
import com.projectManagement.taskflow.entity.UserEntity;
import com.projectManagement.taskflow.repository.CommentRepo;
import com.projectManagement.taskflow.service.AuthService;
import com.projectManagement.taskflow.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("comment_security")
public class CommentSecurity {
    @Autowired
    private AuthService authService;

    @Autowired
    private CommentRepo commentRep;

    public boolean isOwner(Long commentId){
        CommentEntity comment = commentRep.getById(commentId);
        UserEntity requester = authService.getCurrentUser();
        return (requester.equals(comment.getCommentator()));
    }
}
