package com.projectManagement.taskflow.service;

import com.projectManagement.taskflow.dto.CommentRequestDTO;
import com.projectManagement.taskflow.dto.CommentResponseDto;
import com.projectManagement.taskflow.dto.PageResponseDto;
import com.projectManagement.taskflow.entity.CommentEntity;
import com.projectManagement.taskflow.entity.ProfileEntity;
import com.projectManagement.taskflow.entity.TaskEntity;
import com.projectManagement.taskflow.entity.UserEntity;
import com.projectManagement.taskflow.exception.TaskNotFoundException;
import com.projectManagement.taskflow.mapper.CommentMapper;
import com.projectManagement.taskflow.mapper.PageMapper;
import com.projectManagement.taskflow.repository.CommentRepo;
import com.projectManagement.taskflow.repository.TaskRepo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CommentService {

    private final CommentRepo commentRepo;
    private final AuthService authService;
    private final CommentMapper commentMapper;
    private final TaskRepo taskRepo;
    private final PageMapper pageMapper;
    private final ProfileService profileService;

    public CommentService(CommentRepo commentRepo, AuthService authService, CommentMapper commentMapper, TaskRepo taskRepo, PageMapper pageMapper, ProfileService profileService) {
        this.commentRepo = commentRepo;
        this.authService = authService;
        this.commentMapper = commentMapper;
        this.taskRepo = taskRepo;
        this.pageMapper = pageMapper;
        this.profileService = profileService;
    }

    public CommentResponseDto addComment(Long taskId, CommentRequestDTO commentDTO){
        UserEntity author = authService.getCurrentUser();
        ProfileEntity profile = profileService.getProfileByUserId(author.getId());
        TaskEntity task = taskRepo.findById(taskId).orElseThrow(() ->
                new TaskNotFoundException("Task not found")
        );
        CommentEntity comment =  commentMapper.toEntity(commentDTO, author, task);
        comment.setName(profile.getFirstName()+" "+profile.getLastName());

        return commentMapper.toDto(commentRepo.save(comment));
    }

//  TODO : make this pageable also
    @Transactional(readOnly = true)
//    @Cacheable(value = "task-comments", keyGenerator = "tenantKeyGenerator")
    public PageResponseDto<CommentResponseDto> listCommentsForTask(Long taskId, Pageable pageable){
        Page<CommentEntity> comments = commentRepo.findAllByTask_id(taskId, pageable);
        return pageMapper.toDto(comments.map(commentMapper::toDto));
    }

//  TODO: Write logic for failure too
    public String deleteComment(Long id){
        UserEntity requester = authService.getCurrentUser();
        commentRepo.deleteById(id);
        return "Comment Deleted Successfully";
    }
}
