package com.projectManagement.taskflow.controller;

import com.projectManagement.taskflow.dto.*;
import com.projectManagement.taskflow.entity.ProjectEntity;
import com.projectManagement.taskflow.enums.Priority;
import com.projectManagement.taskflow.enums.Status;
import com.projectManagement.taskflow.service.ProjectService;
import com.projectManagement.taskflow.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RequestMapping("/api/projects")
@RestController
public class ProjectsController {

    private final ProjectService projectService;
    private final TaskService taskService;

    public ProjectsController(ProjectService projectService, TaskService taskService) {
        this.projectService = projectService;
        this.taskService = taskService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<List<ProjectResponseDto>> getAllProjects(){
        List<ProjectResponseDto> dtos = projectService.getAllProjects();
        return ResponseEntity.ok(dtos);
    }

    @PostMapping
    public ResponseEntity<ProjectResponseDto> createProject(@Valid @RequestBody ProjectRequestDto project){
        return ResponseEntity.status(HttpStatus.CREATED).body(projectService.createProject(project));
    }

    @PostMapping("/all")
    public ResponseEntity<String> postProjects(@RequestBody List<ProjectEntity> projects){
        projectService.saveALl(projects);
        return ResponseEntity.status(HttpStatus.CREATED).body("Projects Created");
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponseDto> getOneProject(@PathVariable Long id){
        return ResponseEntity.ok(projectService.getProjectById(id));
    }

    @PreAuthorize("hasRole('ADMIN') or project_security.isProjectCreator(#projectId)")
    @PutMapping("/{id}")
    public ResponseEntity<String> updateProject(@PathVariable Long id,@Valid @RequestBody ProjectRequestDto project){
        projectService.updateProject(id, project);
        return ResponseEntity.status(HttpStatus.CREATED).body("Project Created");
    }

    @PreAuthorize("hasRole('ADMIN') or project_security.isProjectCreator(#projectId)")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProject(@PathVariable Long id){
        projectService.deleteProject(id);
        return ResponseEntity.ok("Project Deleted");
    }

    @PreAuthorize("hasRole('ADMIN') or project_security.isProjectCreator(#projectId)")
    @PostMapping("/{id}/members")
    public ResponseEntity<String> addProjectPerMember(@PathVariable Long id,@Valid @RequestBody AssigningUserRequestDto dto){
        projectService.addMember(id, dto.getUserId(), dto.getRoleInProject());
        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }

    @PreAuthorize("hasRole('ADMIN') or project_security.isProjectCreator(#projectId)")
    @DeleteMapping("/{id}/members/{memberId}")
    public ResponseEntity<String> removeMemberFromProject(@PathVariable Long id, @PathVariable Long memberId){
        projectService.removeMember(id, memberId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/tasks")
    public PageResponseDto<TaskResponseDto> getAllTaskOfProject(@RequestParam(defaultValue = "0") int page
            , @RequestParam(defaultValue = "10") int size
            , @PathVariable Long id){
        Pageable pageable = PageRequest.of(page, size);
        return taskService.listTasksByProject(id, pageable);
    }

    @PreAuthorize("hasRole('ADMIN') or project_security.isProjectCreator(#projectId) or project_security.isProjectMember(#projectId)")
    @PostMapping("/{projectId}/tasks")
    public List<TaskResponseDto> postAllTaskOfProject(@PathVariable Long projectId,
                                                      @Valid @RequestBody List<TaskRequestDTO> tasks){
        tasks.forEach((task)-> taskService.createTask(projectId,task));
        List<TaskResponseDto> tasksDto = taskService.getTasksByProjectId(projectId);
        return tasksDto;
    }

    @GetMapping("/users/{projectId}")
    public List<ProjectMemberResponseDto> getAllUsersForAProject(@PathVariable Long projectId ){
        return projectService.listMembersOnAProject(projectId);
    }

    @GetMapping("/user")
    public PageResponseDto<ProjectResponseDto> getProjectsForCurrentUser(
            @RequestParam(required = false) Status status,
            @RequestParam(required = false) Priority priority,
            @RequestParam(required = false) String name,
            @PageableDefault(sort = "id", direction = Sort.Direction.DESC)
            Pageable pageable){
        return projectService.listProjectsForUser(status, priority, name, pageable);
    }
}