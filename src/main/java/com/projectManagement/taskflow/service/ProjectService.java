package com.projectManagement.taskflow.service;

import com.projectManagement.taskflow.dto.*;
import com.projectManagement.taskflow.entity.ProjectEntity;
import com.projectManagement.taskflow.entity.ProjectMember;
import com.projectManagement.taskflow.enums.Priority;
import com.projectManagement.taskflow.enums.RoleEnum;
import com.projectManagement.taskflow.enums.RoleInProject;
import com.projectManagement.taskflow.entity.UserEntity;
import com.projectManagement.taskflow.enums.Status;
import com.projectManagement.taskflow.exception.ProjectNotFoundException;
import com.projectManagement.taskflow.filter.ProjectSpecification;
import com.projectManagement.taskflow.mapper.PageMapper;
import com.projectManagement.taskflow.mapper.ProjectMapper;
import com.projectManagement.taskflow.mapper.ProjectMemberMapper;
import com.projectManagement.taskflow.notification.NotificationEventEnum;
import com.projectManagement.taskflow.notification.NotificationPublisher;
import com.projectManagement.taskflow.repository.ProjectMemberRepo;
import com.projectManagement.taskflow.repository.ProjectRepo;
import com.projectManagement.taskflow.repository.TaskRepo;
import com.projectManagement.taskflow.repository.UserRepo;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProjectService {

    private final UserRepo userrepo;
    private final TaskRepo taskRepo;
    private final PageMapper pageMapper;
    private final UserService userService;
    private final AuthService authService;
    private final ProjectRepo projectRepo;
    private final ProjectMapper projectMapper;
    private final ProjectMemberRepo projectMemberRepo;
    private final ProjectMemberMapper projectMemberMapper;
//    private final RabbitTemplate rabbitTemplate;
    private final NotificationPublisher notificationPublisher;

    public ProjectService(ProjectRepo projectRepo, UserRepo userrepo, ProjectMemberRepo projectMemberRepo, AuthService authService, ProjectMapper projectMapper, ProjectMemberMapper projectMemberMapper, UserService userService, TaskRepo taskRepo, PageMapper pageMapper, RabbitTemplate rabbitTemplate, NotificationPublisher notificationPublisher) {
        this.projectRepo = projectRepo;
        this.userrepo = userrepo;
        this.projectMemberRepo = projectMemberRepo;
        this.authService = authService;
        this.projectMapper = projectMapper;
        this.projectMemberMapper = projectMemberMapper;
        this.userService = userService;
        this.taskRepo = taskRepo;
        this.pageMapper = pageMapper;
//        this.rabbitTemplate = rabbitTemplate;
        this.notificationPublisher = notificationPublisher;
    }

    //TODO: createProject(ProjectEntity project, UserEntity owner) See what it is
    public ProjectResponseDto createProject(ProjectRequestDto dto){
        ProjectEntity entity = new ProjectEntity();
        UserEntity creator = authService.getCurrentUser();
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setStatus(dto.getStatus());
        entity.setUser(creator);

        Map<String, Object> map = new HashMap<>();
        String message = "Project created '" + dto.getName() +"' to project";
        ProjectResponseDto projectResponseDto = projectMapper.toDto(projectRepo.save(entity));
        map.put("projectId",projectResponseDto.getId());
        notificationPublisher.publishNotification(message, projectResponseDto.getId(), map, NotificationEventEnum.PROJECT_CREATED);

        return projectResponseDto;
    }

    public List<ProjectResponseDto> getAllProjects(){
        return projectRepo.findAll().stream().map(projectMapper::toDto).collect(Collectors.toList());
    }

    public List<ProjectEntity> saveALl(List<ProjectEntity> projects){
        return projectRepo.saveAll(projects);
    }

    //TODO: getProjectById(Long id, UserEntity user) See what it
    @Transactional(readOnly = true)
    @Cacheable(
            value = "projects",
            key = "T(com.projectManagement.taskflow.tenant.TenantContext).getTenant() + ':project:' + #id"
    )
    public ProjectResponseDto getProjectById(Long id){
        UserEntity user = authService.getCurrentUser();
        return projectMapper.toDto(
                projectRepo.findById(id)
                .orElseThrow(
                        ()->new RuntimeException("Project Not found")
                )
        );
    }

    @Transactional(readOnly = true)
    public PageResponseDto<ProjectResponseDto> listProjectsForUser(
            Status status,
            Priority priority,
            String name,
            Pageable pageable) {

        UserEntity user = authService.getCurrentUser();

        boolean isAdmin = user.getRole() == RoleEnum.ADMIN;
        Long userId = user.getId();

        Specification<ProjectEntity> spec = ProjectSpecification.filterProjects(
                userId, status, priority, name, isAdmin);

        Page<ProjectEntity> entity = projectRepo.findAll(spec, pageable);

        return pageMapper.toDto(entity.map(projectMapper::toDto));
    }

    @CacheEvict(
            value = "projects",
            key = "T(com.projectManagement.taskflow.tenant.TenantContext).getTenant() + ':project:' + #id"
    )
    public ProjectResponseDto updateProject(Long id, ProjectRequestDto dto){
        UserEntity user = authService.getCurrentUser();
        ProjectEntity entity = projectRepo.findById(id)
                .orElseThrow(()-> new ProjectNotFoundException("Project not found"));
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setStatus(dto.getStatus());
        entity.setUser(user);
        entity.setTasks(taskRepo.findByProject_id(id));

        Map<String, Object> map = new HashMap<>();
        String message = "Project updated '" + dto.getName() +"' to project";
        map.put("projectId",id);
        notificationPublisher.publishNotification(message, id, map, NotificationEventEnum.PROJECT_UPDATED);

        //we don't need to set ProjectMember here because we created add/remove member below
        return projectMapper.toDto(projectRepo.save(entity));
    }

    @CacheEvict(
            value = "projects",
            key = "T(com.projectManagement.taskflow.tenant.TenantContext).getTenant() + ':project:' + #id"
    )
    public boolean deleteProject(Long id){
         authService.getCurrentUser();
         Map<String, Object> map = new HashMap<>();
         String message = "Project deleted with id : '" + id +"'";
         map.put("projectId",id);
         notificationPublisher.publishNotification(message, id, map, NotificationEventEnum.PROJECT_DELETED);

         projectRepo.deleteById(id);
         return true;
    }

    @CacheEvict(
            value = "project-members",
            key = "T(com.projectManagement.taskflow.tenant.TenantContext).getTenant() + ':project-members:' + #projectId"
    )
    public String addMember(Long projectId, Long userId, RoleInProject roleInProject) {

        Optional<ProjectEntity> projectOpt = projectRepo.findById(projectId);
        Optional<UserEntity> userOpt = userrepo.findById(userId);

        if (projectOpt.isEmpty() || userOpt.isEmpty()) {
            return "User or Project not found";
        }

        ProjectMember projectMember = new ProjectMember();
        projectMember.setProject(projectOpt.get());
        projectMember.setUser(userOpt.get());
        projectMember.setRoleInProject(roleInProject);

        projectMemberRepo.save(projectMember);

        Map<String, Object> map = new HashMap<>();
        map.put("projectId",projectOpt.get().getId());
        String message = "New member added " + userOpt.get().getUsername() + " to project " + projectOpt.get().getName();
        notificationPublisher.publishNotification(message, projectId, map, NotificationEventEnum.MEMBER_CHANGED);

        return "User added successfully";
    }

    @CacheEvict(
            value = "project-members",
            key = "T(com.projectManagement.taskflow.tenant.TenantContext).getTenant() + ':project-members:' + #projectId"
    )
    public boolean removeMember(Long projectId, Long memberId){
        UserEntity requester = authService.getCurrentUser();
        Map<String, Object> map = new HashMap<>();
        String message = "Member deleted " +memberId +" to project ";
        map.put("memberId",memberId);
        notificationPublisher.publishNotification(message, projectId, map, NotificationEventEnum.MEMBER_DELETED);
        projectMemberRepo.deleteById(memberId);
        return true;
    }

    @Transactional(readOnly = true)
    @Cacheable(
            value = "project-members",
            key = "T(com.projectManagement.taskflow.tenant.TenantContext).getTenant() + ':project-members:' + #projectId"
    )
    public List<UserResponseDto> listAllUsersOnAProject(Long projectId){
        List<ProjectMemberResponseDto> projectMembersDto = listMembersOnAProject(projectId);
        List<UserResponseDto> userDtos = projectMembersDto.stream()
                .map((member)->userService.findById(member.getUser().getId()))
                .collect(Collectors.toList());
        return userDtos;
    }

    @Transactional(readOnly = true)
    @Cacheable(
            value = "project-members",
            key = "T(com.projectManagement.taskflow.tenant.TenantContext).getTenant() + ':project-members:' + #projectId"
    )
    public List<ProjectMemberResponseDto> listMembersOnAProject(Long projectId){
        return projectMemberRepo.findAllByProject_id(projectId)
                .stream()
                .map(projectMemberMapper::toDto)
                .collect(Collectors.toList());
    }
}
