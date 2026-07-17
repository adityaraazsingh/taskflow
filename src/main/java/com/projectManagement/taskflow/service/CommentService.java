package com.projectManagement.taskflow.service;

import com.projectManagement.taskflow.dto.CommentRequestDTO;
import com.projectManagement.taskflow.dto.CommentResponseDto;
import com.projectManagement.taskflow.dto.TaskRequestDTO;
import com.projectManagement.taskflow.entity.CommentEntity;
import com.projectManagement.taskflow.entity.TaskEntity;
import com.projectManagement.taskflow.entity.UserEntity;
import com.projectManagement.taskflow.mapper.CommentMapper;
import com.projectManagement.taskflow.repository.CommentRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {

    @Autowired
    private CommentRepo commentRepo;

    @Autowired
    private TaskService taskService;

    @Autowired
    private AuthService authService;

    @Autowired
    private CommentMapper commentMapper;

    public String addComment(Long taskId, CommentRequestDTO commentDTO){
        UserEntity author = authService.getCurrentUser();
        TaskEntity task = taskService.getTaskById(taskId);
        CommentEntity comment =  commentMapper.toEntity(commentDTO, author, task); ;
        commentRepo.save(comment);
        return "Comment Added Succesfully";
    }

//    TODO : make this pageable also
    public Page<CommentResponseDto> listCommentsForTask(Long taskId, Pageable pageable){
        Page<CommentEntity> comments = commentRepo.findAllByTask_id(taskId, pageable);
        return comments.map(commentMapper::toDto);
    }

//    TODO: Write logic for failure too
    public String deleteComment(Long id){
        UserEntity requester = authService.getCurrentUser();
        commentRepo.deleteById(id);
        return "Comment Deleted Successfully";
    }
}
