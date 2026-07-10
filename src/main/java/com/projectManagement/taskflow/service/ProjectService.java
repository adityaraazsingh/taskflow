package com.projectManagement.taskflow.service;

import com.projectManagement.taskflow.entity.ProjectEntity;
import com.projectManagement.taskflow.entity.ProjectMember;
import com.projectManagement.taskflow.enums.RoleInProject;
import com.projectManagement.taskflow.entity.UserEntity;
import com.projectManagement.taskflow.repository.ProjectMemberRepo;
import com.projectManagement.taskflow.repository.ProjectRepo;
import com.projectManagement.taskflow.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

//TODO: createProject(ProjectEntity project, UserEntity owner) See what it is
    public ProjectEntity createProject(ProjectEntity project){
       return projectRepo.save(project);
    }

    //TODO: getProjectById(Long id, UserEntity user) See what it is
    public ProjectEntity getProjectById(Long id){
        UserEntity user = authService.getCurrentUser();
        return projectRepo.findById(id)
                .orElseThrow(()->new RuntimeException("Project Not found"));
    }

    public Page<ProjectEntity> listProjectsForUser(Long userId, Pageable pageable){
        return projectRepo.findByUser_id(userId,pageable);
    }

    public ProjectEntity updateProject(Long id, ProjectEntity updatedProjectRequest, UserEntity use){
        return projectRepo.save(updatedProjectRequest);
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

    public List<ProjectMember> listMembers(Long projectId){
        return projectRepo.findAllById(projectId);
    }
}
