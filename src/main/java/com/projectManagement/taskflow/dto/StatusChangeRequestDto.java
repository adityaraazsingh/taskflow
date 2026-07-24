package com.projectManagement.taskflow.dto;

import com.projectManagement.taskflow.enums.Status;

public class StatusChangeRequestDto {
    private Status status;

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
