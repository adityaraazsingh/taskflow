package com.projectManagement.taskflow.dto;

import com.projectManagement.taskflow.enums.Priority;

public class PriorityChangeRequestDto {
    private Priority priority;

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }
}
