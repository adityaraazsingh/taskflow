package com.projectManagement.taskflow.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CommentRequestDTO {
    @NotNull
    private String name;

    @NotNull
    private String content;
}
