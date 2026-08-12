package com.projectManagement.taskflow.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TagRequestDTO {
    @NotNull(message = "name can't be null")
    private String name;
    private String colorHex;
}
