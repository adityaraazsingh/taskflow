package com.projectManagement.taskflow.dto;

import lombok.Data;

import java.util.Date;

@Data
public class CommentResponseDto {
    private Long id;
    private String name;
    private String content;
    private Long taskId;
    private Long userId;
    private Date createdAt;
}
