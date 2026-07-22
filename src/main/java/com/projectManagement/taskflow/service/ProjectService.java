package com.projectManagement.taskflow.service;

import com.projectManagement.taskflow.dto.ProjectMemberResponseDto;
import com.projectManagement.taskflow.dto.ProjectRequestDto;
import com.projectManagement.taskflow.dto.ProjectResponseDto;
import com.projectManagement.taskflow.entity.ProjectEntity;
import com.projectManagement.taskflow.entity.ProjectMember;
import com.projectManagement.taskflow.enums.RoleInProject;
import com.projectManagement.taskflow.entity.UserEntity;
import com.projectManagement.taskflow.exception.ProjectNotFoundException;
import com.projectManagement.taskflow.exception.UserNotFoundException;
import com.projectManagement.taskflow.mapper.ProjectMapper;
import com.projectManagement.taskflow.mapper.ProjectMemberMapper;
import com.projectManagement.taskflow.repository.ProjectMemberRepo;
import com.projectManagement.taskflow.repository.ProjectRepo;
import com.projectManagement.taskflow.repository.TaskRepo;
import com.projectManagement.taskflow.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepo projectRepo;

    @Autowired
    private UserRepo userrepo;

    @Autowired
    private ProjectMemberRepo projectMemberRepo;

    @Autowired
    private AuthService authService;

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private ProjectMemberMapper projectMemberMapper;

    @Autowired
    private TaskRepo taskRepo;

//TODO: createProject(ProjectEntity project, UserEntity owner) See what it is
    public ProjectResponseDto createProject(ProjectRequestDto dto){
        ProjectEntity entity = new ProjectEntity();
        UserEntity creator = authService.getCurrentUser();
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setStatus(dto.getStatus());
        entity.setUser(creator);
// TODO : We are not setting taskIds and ProjectMemberIDs, creator should be the first projectMember
        return projectMapper.toDto(projectRepo.save(entity));
    }

    //TODO: getProjectById(Long id, UserEntity user) See what it is
    public ProjectResponseDto getProjectById(Long id){
        UserEntity user = authService.getCurrentUser();
        return projectMapper.toDto(
                projectRepo.findById(id)
                .orElseThrow(
                        ()->new RuntimeException("Project Not found")
                )
        );
    }

    public Page<ProjectResponseDto> listProjectsForUser(Long userId, Pageable pageable){
        Page<ProjectEntity> entity = projectRepo.findByUser_id(userId,pageable);
        return entity.map(projectMapper::toDto);
    }

    public ProjectResponseDto updateProject(Long id, ProjectRequestDto dto){
        UserEntity user = authService.getCurrentUser();
        ProjectEntity entity = projectRepo.findById(id)
                .orElseThrow(()-> new ProjectNotFoundException("Project not found"));
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setStatus(dto.getStatus());
        entity.setUser(user);
        entity.setTasks(taskRepo.findByProject_id(id));
        //we don't need to set ProjectMember here because we created add/remove member below
        return projectMapper.toDto(projectRepo.save(entity));
    }

    public boolean deleteProject(Long id){
         authService.getCurrentUser();
         projectRepo.deleteById(id);
         return true;
    }

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

        return "User added successfully";
    }

    public boolean removeMember(Long projectId, Long userId){
        UserEntity requester = authService.getCurrentUser();

        ProjectMember projectMember = projectMemberRepo.findByUser_idAndProject_id(userId , projectId);
        projectMemberRepo.delete(projectMember);
        return true;
    }

    public List<ProjectMemberResponseDto> listMembers(Long projectId){
        return projectMemberRepo.findAllByProject_id(projectId)
                .stream()
                .map(projectMemberMapper::toDto)
                .collect(Collectors.toList());
    }
}
