package com.projectManagement.taskflow.mapper;

import com.projectManagement.taskflow.dto.ProjectMemberResponseDto;
import com.projectManagement.taskflow.dto.ProjectRequestDto;
import com.projectManagement.taskflow.entity.ProjectMember;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

@Component
public class ProjectMemberMapper {
    @Autowired
    private UserMapper userMapper;
   public ProjectMemberResponseDto toDto(ProjectMember entity){
       ProjectMemberResponseDto dto = new ProjectMemberResponseDto();
       dto.setId(entity.getId());
       dto.setUser(userMapper.toDto(entity.getUser()));
       dto.setProjectId(entity.getProject().getId());
       dto.setRoleInProject(entity.getRoleInProject());
       return dto;
   }

   public ProjectMember toEntity(ProjectRequestDto dto){
       ProjectMember entity = new ProjectMember();
       return entity;
   }
}
