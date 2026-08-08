package com.projectManagement.taskflow.security;

import com.projectManagement.taskflow.dto.ProjectResponseDto;
import com.projectManagement.taskflow.dto.TaskResponseDto;
import com.projectManagement.taskflow.entity.ProjectEntity;
import com.projectManagement.taskflow.entity.TaskEntity;
import com.projectManagement.taskflow.entity.UserEntity;
import com.projectManagement.taskflow.service.AuthService;
import com.projectManagement.taskflow.service.ProjectService;
import com.projectManagement.taskflow.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("project_security")
public class ProjectSecurity {

    @Autowired
    private AuthService authService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private TaskService taskService;

    public boolean isProjectCreatorFromTaskId(Long taskId){
        TaskResponseDto project = taskService.getTaskById(taskId);
        if(project == null) return false;

        return isProjectCreator(project.getProjectId());
    }

    public boolean isProjectMemberFromTaskId(Long taskId){
        TaskResponseDto project = taskService.getTaskById(taskId);
        if(project == null) return false;

        return isProjectMember(project.getProjectId());
    }



    public boolean isProjectCreator(Long projectId){
        Long userIdOfProject = projectService.getProjectById(projectId).getUserId();
        Long userId = authService.getCurrentUser().getId();
        return userId.equals(userIdOfProject);
    }

    public boolean isProjectMember(Long projectId){
        ProjectResponseDto project = projectService.getProjectById(projectId);
        UserEntity user = authService.getCurrentUser();
        for(Long a : project.getProjectMemberIds()){
            if(a.equals(user.getId())){
                return true;
            }
        }
        return false;
    }
}
