package com.projectManagement.taskflow.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CommentRequestDTO {
    @NotNull(message = "name can't be null")
    private String name;

    @NotNull(message = "content can't be null")
    private String content;
}
