package com.projectManagement.taskflow.controller;

import com.projectManagement.taskflow.dto.TaskRequestDTO;
import com.projectManagement.taskflow.entity.ProjectEntity;
import com.projectManagement.taskflow.entity.TaskEntity;
import com.projectManagement.taskflow.entity.UserEntity;
import com.projectManagement.taskflow.enums.RoleInProject;
import com.projectManagement.taskflow.repository.ProjectRepo;
import com.projectManagement.taskflow.repository.TaskRepo;
import com.projectManagement.taskflow.service.AuthService;
import com.projectManagement.taskflow.service.ProjectService;
import com.projectManagement.taskflow.service.TaskService;
import com.projectManagement.taskflow.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/projects")
@RestController
public class ProjectsController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private ProjectRepo projectRepo;

    @Autowired
    private UserService userService;

    @Autowired
    private TaskRepo taskRepo;

    @Autowired
    private AuthService authService;

    @GetMapping
    public List<ProjectEntity> getProjects(){
        return projectRepo.findAll();
    }

    @PostMapping
    public boolean postProjects(@RequestBody List<ProjectEntity> projects){
        projectRepo.saveAll(projects);
        return true;
    }

    @GetMapping("/{id}")
    public ProjectEntity getOneProject(@PathVariable Long id){
        return projectService.getProjectById(id);
    }

    @PutMapping("/{id}")
    public boolean createOrUpdateProject(@PathVariable Long id, @RequestBody ProjectEntity project){
        projectService.createProject(project);
        return true;
    }

    @DeleteMapping("/{id}")
    public boolean deleteProject(@PathVariable Long id){
        projectService.deleteProject(id);
        return true;
    }

    //POST: /api/projects/{id}/members
    //TODO: NOT DONE COMPLETE IT
    //    TODO: user fetching logic is incorrect
    //        "EDITOR" just send this nothing more than it
    @PostMapping("/{id}/members")
    public boolean addProjectPerMember(@PathVariable Long id, @RequestBody RoleInProject role){
        UserEntity user = userService.findByUsername("Aditya");
        projectService.addMember(id,user.getId(),role);
        return true;
    }

    @DeleteMapping("/{id}/members/{userId}")
    public boolean deleteProjectForMember(@PathVariable Long id, @PathVariable Long userId){
        return projectService.removeMember(userId, id);
    }

    @GetMapping("/{id}/tasks")
    public Page<TaskEntity> getAllTaskOfProject(@RequestParam(defaultValue = "0") int page
            , @RequestParam(defaultValue = "10") int size
            , @PathVariable Long id){
        Pageable pageable = PageRequest.of(page, size);
        return taskService.listTasksByProject(id, pageable);
    }

    @PostMapping("/{id}/tasks")
    public List<TaskEntity> postAllTaskOfProject(@PathVariable Long id,
                                                 @RequestBody List<TaskRequestDTO> tasks){
//        id = projectId here
        UserEntity user = authService.getCurrentUser();
        tasks.forEach((task)-> taskService.createTask(id,task,user));
        return taskRepo.findByProject_id(id);
    }

    @GetMapping("/user/{userId}")
    private Page<ProjectEntity> getProjectsForUser(@PathVariable Long userId,
                                                   @RequestParam(defaultValue = "0") int size,
                                                   @RequestParam(defaultValue = "10") int page) {
        Pageable pageable = PageRequest.of(page,size);
        return projectService.listProjectsForUser(userId, pageable);
    }
}