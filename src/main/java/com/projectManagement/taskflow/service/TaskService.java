package com.projectManagement.taskflow.service;

import com.projectManagement.taskflow.dto.PageResponseDto;
import com.projectManagement.taskflow.dto.TaskRequestDTO;
import com.projectManagement.taskflow.dto.TaskResponseDto;
import com.projectManagement.taskflow.dto.UserRequestDTO;
import com.projectManagement.taskflow.entity.*;
import com.projectManagement.taskflow.enums.Priority;
import com.projectManagement.taskflow.enums.Status;
import com.projectManagement.taskflow.exception.ProjectNotFoundException;
import com.projectManagement.taskflow.exception.TaskNotFoundException;
import com.projectManagement.taskflow.exception.UserNotFoundException;
import com.projectManagement.taskflow.mapper.PageMapper;
import com.projectManagement.taskflow.mapper.TaskMapper;
import com.projectManagement.taskflow.notification.NotificationEvent;
import com.projectManagement.taskflow.notification.NotificationEventEnum;
import com.projectManagement.taskflow.notification.TaskAssignedData;
import com.projectManagement.taskflow.repository.ProjectRepo;
import com.projectManagement.taskflow.repository.TaskRepo;
import com.projectManagement.taskflow.repository.UserRepo;
import com.projectManagement.taskflow.tenant.TenantContext;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TaskService {

    private final TaskRepo taskRepo;
    private final ProjectRepo projectRepo;
    private final UserRepo userRepo;
    private final AuthService authService;
    private final TaskMapper taskMapper;
    private final PageMapper pageMapper;
    private final RabbitTemplate rabbitTemplate;

    public TaskService(TaskRepo taskRepo, ProjectRepo projectRepo, UserRepo userRepo, AuthService authService, TaskMapper taskMapper, PageMapper pageMapper, RabbitTemplate rabbitTemplate) {
        this.taskRepo = taskRepo;
        this.projectRepo = projectRepo;
        this.userRepo = userRepo;
        this.authService = authService;
        this.taskMapper = taskMapper;
        this.pageMapper = pageMapper;
        this.rabbitTemplate = rabbitTemplate;
    }


    public TaskEntity createTask(Long projectId , TaskRequestDTO taskDTO){
        ProjectEntity project = projectRepo.findById(projectId)
                .orElseThrow(()-> new ProjectNotFoundException("Project Not Found"));
        UserEntity user = authService.getCurrentUser();
        TaskEntity task = taskMapper.toEntity(taskDTO,project,user);
        Date now = new Date();
        task.setCreatedAt(now);
        task.setUpdatedAt(now);

        return taskRepo.save(task);
    }

    @Transactional(readOnly = true)
    @Cacheable(
            value = "project-task",
            key = "T(com.projectManagement.taskflow.tenant.TenantContext).getTenant() + ':project-task:' + #id"
    )
    public TaskResponseDto getTaskById(Long id){
        UserEntity user = authService.getCurrentUser();
        TaskEntity task = taskRepo.findById(id)
                .orElseThrow(()-> new TaskNotFoundException("Task Not Found"));
        return taskMapper.toDto(task);
    }

//TODO: Add TaskFilter

    @Transactional(readOnly = true)
    public PageResponseDto<TaskResponseDto> listTasksByProject(Long projectId, Pageable pageable){
        Page<TaskEntity> tasks = taskRepo.findByProject_id(projectId, pageable);
        return pageMapper.toDto(tasks.map(taskMapper::toDto));
    }

//TODO:    Add logic of Role of User according To projectMember
    @CacheEvict(
            value = "project-task",
            key = "T(com.projectManagement.taskflow.tenant.TenantContext).getTenant() + ':project-task:' + #id"
    )
    public TaskResponseDto updateTask(Long id ,TaskRequestDTO updateTaskRequest){
        UserEntity requester = authService.getCurrentUser();
        TaskEntity entity = taskRepo.findById(id).orElseThrow(()-> new TaskNotFoundException("Task not found"));
        entity.setTitle(updateTaskRequest.getTitle());
        entity.setDescription(updateTaskRequest.getDescription());
        entity.setPriority(updateTaskRequest.getPriority());
        entity.setStatus(updateTaskRequest.getStatus());
        entity.setDueDate(updateTaskRequest.getDueDate());

        return taskMapper.toDto(taskRepo.save(entity));
    }

    @CacheEvict(
            value = "project-task",
            key = "T(com.projectManagement.taskflow.tenant.TenantContext).getTenant() + ':project-task:' + #id"
    )
    public String updateStatus(Long id , Status status){
        UserEntity user = authService.getCurrentUser();
        TaskEntity task = taskRepo.findById(id)
                .orElseThrow(()->new RuntimeException("Task Not Found"));
        task.setStatus(status);
        taskRepo.save(task);
        return "Status Updated to "+task.getStatus();
    }

    @CacheEvict(
            value = "project-task",
            key = "T(com.projectManagement.taskflow.tenant.TenantContext).getTenant() + ':project-task:' + #id"
    )
    public String updatePriority(Long id , Priority priority){
        UserEntity user = authService.getCurrentUser();
        TaskEntity task = taskRepo.findById(id)
                .orElseThrow(()->new RuntimeException("Task Not Found"));
        task.setPriority(priority);
        taskRepo.save(task);
        return "Status Updated to "+task.getStatus();
    }

//   TODO : userId is not being used assignTask(Long id, Long userId ,UserEntity user)
    @CacheEvict(
            value = "project-task",
            key = "T(com.projectManagement.taskflow.tenant.TenantContext).getTenant() + ':project-task:' + #id"
    )
    public String assignTask(Long id, UserRequestDTO dto){
        TaskEntity task = taskRepo.findById(id)
                .orElseThrow(()->new RuntimeException("Task Not Found"));
        UserEntity user = userRepo.findByUsername(dto.getUsername()).orElseThrow(()-> new UserNotFoundException("User with 'Username' Not found"));
        user.getTasks().add(task);

        userRepo.save(user);
        task.setAssignee(user);
        taskRepo.save(task);

        // Create event data
//        TaskAssignedData data = new TaskAssignedData(
//                task.getId(),
//                task.getTitle(),
//                user.getId(),
//                user.getUsername()
//        );
//
//        // Create event
//        NotificationEvent event =
//                new NotificationEvent(
//                        NotificationEventEnum.TASK_CREATED,
//                        TenantContext.getTenant(),
//                        data.toString()
//                );
//
//        // Publish
//        rabbitTemplate.convertAndSend(
//                "task.exchange",
//                "task.assigned",
//                event
//        );

        return "Task is assigned to user with user id : "+user.getId();
    }

//    TODO : logic of above and below code deals with positive cases only
//    TODO : deleteTask(Long id , UserEntity requester)
    @CacheEvict(
            value = "project-task",
            key = "T(com.projectManagement.taskflow.tenant.TenantContext).getTenant() + ':project-task:' + #id"
    )
    public String deleteTask(Long id){
        taskRepo.deleteById(id);
        return "Task with "+id+" deleted successfully";
    }

    public List<TaskResponseDto> getTasksByProjectId(Long projectId){
        return taskRepo.findByProject_id(projectId)
                .stream()
                .map(task-> taskMapper.toDto(task))
                .collect(Collectors.toList());
    }

}
