package com.projectManagement.taskflow.dto;

import lombok.Data;

import java.util.List;

@Data
public class PageResponseDto<T> {
    List<T> content;
    int page;
    int size;
    long totalElements;
    int totalPages;
}