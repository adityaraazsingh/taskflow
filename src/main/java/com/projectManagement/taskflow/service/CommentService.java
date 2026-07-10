package com.projectManagement.taskflow.service;

import com.projectManagement.taskflow.dto.CommentRequestDTO;
import com.projectManagement.taskflow.entity.CommentEntity;
import com.projectManagement.taskflow.entity.UserEntity;
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

    public String addComment(Long taskId, CommentRequestDTO commentDTO, UserEntity author){
        CommentEntity comment = new CommentEntity() ;
        comment.setName(commentDTO.name);
        comment.setContent(commentDTO.content);
        comment.setCommentator(author);
        comment.setTask(taskService.getTaskById(taskId));

        commentRepo.save(comment);
        return "Comment Added Succesfully";
    }

//    TODO : make this pageable also
    public Page<CommentEntity> listCommentsForTask(Long taskId, Pageable pageable){
        return commentRepo.findAllByTask_id(taskId, pageable);
    }

//    TODO: Write logic for failure too
//    TODO : write logic for 'deleteComment(Long id, UserEntity requester)'
    public String deleteComment(Long id){
        commentRepo.deleteById(id);
        return "Comment Deleted Successfully";
    }
}
