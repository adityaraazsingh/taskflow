package com.projectManagement.taskflow.service;

import com.projectManagement.taskflow.dto.CommentRequestDTO;
import com.projectManagement.taskflow.entity.CommentEntity;
import com.projectManagement.taskflow.entity.UserEntity;
import com.projectManagement.taskflow.repository.CommentRepo;
import org.springframework.beans.factory.annotation.Autowired;
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
        comment.setTask(taskService.getTaskById(taskId,author));

        commentRepo.save(comment);
        return "Comment Added Succesfully";
    }
//    TODO : make this pageable also
    public List<CommentEntity> listCommentForTask(Long taskId){
        return commentRepo.findAllByTask_id(taskId);
    }
//    TODO: Write logic for failure too
//    TODO : write logic for 'deleteComment(Long id, UserEntity requester)'
    public String deleteComment(Long id){
        commentRepo.deleteById(id);
        return "Comment Deleted Successfully";
    }
}
