package com.projectManagement.taskflow.service;

import com.projectManagement.taskflow.dto.TaskRequestDTO;
import com.projectManagement.taskflow.entity.ProjectEntity;
import com.projectManagement.taskflow.entity.TaskEntity;
import com.projectManagement.taskflow.entity.UserEntity;
import com.projectManagement.taskflow.enums.Status;
import com.projectManagement.taskflow.repository.ProjectRepo;
import com.projectManagement.taskflow.repository.TaskRepo;
import com.projectManagement.taskflow.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    @Autowired
    private TaskRepo taskRepo;
    @Autowired
    private ProjectRepo projectRepo;

    @Autowired
    private UserRepo userRepo;

//    TODO : write logic for requester createTask(Long projectId , TaskRequestDTO taskDTO, UserEntity requester)

    public TaskEntity createTask(Long projectId , TaskRequestDTO taskDTO, UserEntity requester){
        TaskEntity task = new TaskEntity();
//    TODO :Create Exceptions for project not found etc.....
        ProjectEntity project = projectRepo.findById(projectId)
                .orElseThrow(()-> new RuntimeException("Project Not Found"));
        task.setProject(project);

        task.setDescription(taskDTO.getDescription());
        task.setTitle(taskDTO.getTitle());
        task.setDueDate(taskDTO.getDueDate());
        task.setAssignee(requester);
        // Not saving comments and Tags
        task.setStatus(Status.TODO);
        task.setPriority(taskDTO.getPriority());

        Date now = new Date();
        task.setCreatedAt(now);
        task.setUpdatedAt(now);

        return taskRepo.save(task);
    }
//TODO : fix getTaskById(Long id , UserEntity user)
    public TaskEntity getTaskById(Long id , UserEntity user){
//        TODO : Create Custom Error Exception For Task Not Found
        return taskRepo.findById(id)
                .orElseThrow(()-> new RuntimeException("Task Not Found"));
    }

//TODO: Add TaskFilter and Pageable 'listTasksByProject(projectId, taskFilter, Pageable)'
    public List<TaskEntity> listTasksByProject(Long projectId){
        return taskRepo.findByProject_id(projectId);
    }

//TODO:    Add logic of Role of User according To projectMember
    public TaskEntity updateTask(Long id ,TaskEntity updateTaskRequest,UserEntity requester){
        return taskRepo.save(updateTaskRequest);
    }

    public String updateStatus(Long id , Status status, UserEntity requester){
        TaskEntity task = taskRepo.findById(id)
                .orElseThrow(()->new RuntimeException("Task Not Found"));
        task.setStatus(status);
        taskRepo.save(task);
        return "Status Updated to "+task.getStatus();
    }
//TODO : userId is not being used assignTask(Long id, Long userId ,UserEntity user)
    public String assignTask(Long id, UserEntity user){
        TaskEntity task = taskRepo.findById(id)
                .orElseThrow(()->new RuntimeException("Task Not Found"));
        user.getTasks().add(task);
        userRepo.save(user);
        task.setAssignee(user);
        taskRepo.save(task);
        return "Task is assigned to user with user id : "+user.getId();
    }

//    TODO: logic of above and below code deals with positive cases only
//    TODO : deleteTask(Long id , UserEntity requester)
    public String deleteTask(Long id){
        taskRepo.deleteById(id);
        return "User with "+id+" deleted successfully";
    }

}
