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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<String> postProjects(@RequestBody List<ProjectEntity> projects){
        projectRepo.saveAll(projects);
        return ResponseEntity.status(HttpStatus.CREATED).body("Projects Created");
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectEntity> getOneProject(@PathVariable Long id){
        return ResponseEntity.ok(projectService.getProjectById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> createOrUpdateProject(@PathVariable Long id, @RequestBody ProjectEntity project){
        projectService.createProject(project);
        return ResponseEntity.status(HttpStatus.CREATED).body("Project Created");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProject(@PathVariable Long id){
        projectService.deleteProject(id);
        return ResponseEntity.ok("Project Deleted");
    }

    //POST: /api/projects/{id}/members
    //TODO: NOT DONE COMPLETE IT
    //    TODO: user fetching logic is incorrect
    //        "EDITOR" just send this nothing more than it
    @PostMapping("/{id}/members")
    public ResponseEntity<String> addProjectPerMember(@PathVariable Long id, @RequestBody RoleInProject role){
        UserEntity user = userService.findByUsername("Aditya");
        projectService.addMember(id,user.getId(),role);
        return ResponseEntity.ok("Member added to the Project with id "+ id);
    }

    @DeleteMapping("/{id}/members/{userId}")
    public ResponseEntity<String> deleteProjectForMember(@PathVariable Long id, @PathVariable Long userId){
        projectService.removeMember(userId, id);
        return ResponseEntity.ok("Member removed from Project");
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