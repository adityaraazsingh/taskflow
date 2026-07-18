package com.projectManagement.taskflow.controller;

import com.projectManagement.taskflow.dto.ProjectRequestDto;
import com.projectManagement.taskflow.dto.ProjectResponseDto;
import com.projectManagement.taskflow.dto.TaskRequestDTO;
import com.projectManagement.taskflow.dto.TaskResponseDto;
import com.projectManagement.taskflow.entity.ProjectEntity;
import com.projectManagement.taskflow.entity.UserEntity;
import com.projectManagement.taskflow.enums.RoleInProject;
import com.projectManagement.taskflow.mapper.ProjectMapper;
import com.projectManagement.taskflow.mapper.TaskMapper;
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
import java.util.stream.Collectors;

@RequestMapping("/api/projects")
@RestController
public class ProjectsController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private ProjectRepo projectRepo;

    @Autowired
    private UserService userService;

    @Autowired
    private TaskRepo taskRepo;

    @Autowired
    private AuthService authService;

    @Autowired
    private ProjectMapper projectMapper;

    @GetMapping
    public List<ProjectEntity> getProjects(){
        return projectRepo.findAll();
    }

    @PostMapping
    public ResponseEntity<ProjectResponseDto> createProject(@RequestBody ProjectRequestDto project){
        return ResponseEntity.status(HttpStatus.CREATED).body(projectService.createProject(project));
    }

    @PostMapping("/all")
    public ResponseEntity<String> postProjects(@RequestBody List<ProjectEntity> projects){
        projectRepo.saveAll(projects);
        return ResponseEntity.status(HttpStatus.CREATED).body("Projects Created");
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponseDto> getOneProject(@PathVariable Long id){
        return ResponseEntity.ok(projectService.getProjectById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateProject(@PathVariable Long id, @RequestBody ProjectRequestDto project){
        projectService.updateProject(id, project);
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
        UserEntity user = authService.getCurrentUser();
        projectService.addMember(id,user.getId(),role);
        return ResponseEntity.ok("Member added to the Project with id "+ id);
    }

    @DeleteMapping("/{id}/members/{userId}")
    public ResponseEntity<String> deleteProjectForMember(@PathVariable Long id, @PathVariable Long userId){
        projectService.removeMember(userId, id);
        return ResponseEntity.ok("Member removed from Project");
    }

    @GetMapping("/{id}/tasks")
    public Page<TaskResponseDto> getAllTaskOfProject(@RequestParam(defaultValue = "0") int page
            , @RequestParam(defaultValue = "10") int size
            , @PathVariable Long id){
        Pageable pageable = PageRequest.of(page, size);
        return taskService.listTasksByProject(id, pageable);
    }

    @PostMapping("/{projectId}/tasks")
    public List<TaskResponseDto> postAllTaskOfProject(@PathVariable Long projectId,
                                                      @RequestBody List<TaskRequestDTO> tasks){

        tasks.forEach((task)-> taskService.createTask(projectId,task));
        List<TaskResponseDto> tasksDto = taskRepo.findByProject_id(projectId)
                .stream()
                .map(task-> taskMapper.toDto(task))
                .collect(Collectors.toList());

        return tasksDto;
    }

    @GetMapping("/user/{userId}")
    private Page<ProjectResponseDto> getProjectsForUser(@PathVariable Long userId,
                                                   @RequestParam(defaultValue = "0") int size,
                                                   @RequestParam(defaultValue = "10") int page) {
        Pageable pageable = PageRequest.of(page,size);
        return projectService.listProjectsForUser(userId, pageable);
    }
}