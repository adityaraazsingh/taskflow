package com.projectManagement.taskflow.security;

import com.projectManagement.taskflow.dto.TaskResponseDto;
import com.projectManagement.taskflow.entity.TaskEntity;
import com.projectManagement.taskflow.entity.UserEntity;
import com.projectManagement.taskflow.service.AuthService;
import com.projectManagement.taskflow.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("tasks_security")
public class TasksSecurity {

    @Autowired
    private AuthService authService;

    @Autowired
    private TaskService taskService;

    public boolean isAssignedToTask(Long taskId){
        TaskResponseDto task = taskService.getTaskById(taskId);
        Long userId = authService.getCurrentUser().getId();
        return (userId.equals(task.getAssigneeId()));
    }

}
