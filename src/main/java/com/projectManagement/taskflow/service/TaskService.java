package com.projectManagement.taskflow.service;

import com.projectManagement.taskflow.dto.TaskRequestDTO;
import com.projectManagement.taskflow.dto.TaskResponseDto;
import com.projectManagement.taskflow.dto.UserRequestDTO;
import com.projectManagement.taskflow.dto.UserResponseDto;
import com.projectManagement.taskflow.entity.*;
import com.projectManagement.taskflow.enums.Status;
import com.projectManagement.taskflow.exception.ProjectNotFoundException;
import com.projectManagement.taskflow.exception.TaskNotFoundException;
import com.projectManagement.taskflow.exception.UserNotFoundException;
import com.projectManagement.taskflow.mapper.TaskMapper;
import com.projectManagement.taskflow.mapper.UserMapper;
import com.projectManagement.taskflow.repository.ProjectRepo;
import com.projectManagement.taskflow.repository.TaskRepo;
import com.projectManagement.taskflow.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class TaskService {

    @Autowired
    private TaskRepo taskRepo;
    @Autowired
    private ProjectRepo projectRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private AuthService authService;

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private UserMapper userMapper;

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

    public TaskResponseDto getTaskById(Long id){
        UserEntity user = authService.getCurrentUser();
        TaskEntity task = taskRepo.findById(id)
                .orElseThrow(()-> new TaskNotFoundException("Task Not Found"));
        return taskMapper.toDto(task);
    }

//TODO: Add TaskFilter
    public Page<TaskResponseDto> listTasksByProject(Long projectId, Pageable pageable){
        Page<TaskEntity> tasks = taskRepo.findByProject_id(projectId, pageable);
        return tasks.map(taskMapper::toDto);
    }

//TODO:    Add logic of Role of User according To projectMember
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

    public String updateStatus(Long id , Status status){
        UserEntity user = authService.getCurrentUser();
        TaskEntity task = taskRepo.findById(id)
                .orElseThrow(()->new RuntimeException("Task Not Found"));
        task.setStatus(status);
        taskRepo.save(task);
        return "Status Updated to "+task.getStatus();
    }

//   TODO : userId is not being used assignTask(Long id, Long userId ,UserEntity user)
    public String assignTask(Long id, UserRequestDTO dto){
        TaskEntity task = taskRepo.findById(id)
                .orElseThrow(()->new RuntimeException("Task Not Found"));
        UserEntity user = userRepo.findByUsername(dto.getUsername()).orElseThrow(()-> new UserNotFoundException("User with 'Username' Not found"));
        user.getTasks().add(task);
        userRepo.save(user);
        task.setAssignee(user);
        taskRepo.save(task);
        return "Task is assigned to user with user id : "+user.getId();
    }

//    TODO : logic of above and below code deals with positive cases only
//    TODO : deleteTask(Long id , UserEntity requester)
    public String deleteTask(Long id){
        taskRepo.deleteById(id);
        return "User with "+id+" deleted successfully";
    }

}
